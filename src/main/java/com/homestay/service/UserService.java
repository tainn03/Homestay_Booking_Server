package com.homestay.service;

import com.homestay.constants.Roles;
import com.homestay.constants.UserStatus;
import com.homestay.dto.request.ReviewRequest;
import com.homestay.dto.request.UpdateUserRequest;
import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.ReviewResponse;
import com.homestay.dto.response.UserResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.UserMapper;
import com.homestay.model.*;
import com.homestay.repository.HomestayRepository;
import com.homestay.repository.ImageRepository;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.UserRepository;
import com.homestay.service.external.CloudinaryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    CloudinaryService cloudinaryService;
    ImageRepository imageRepository;
    private final HomestayRepository homestayRepository;

    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        User user = userMapper.toUser(request);
        user.setStatus(UserStatus.ACTIVE.name());
        if (request.getRole() == null || request.getRole().isEmpty()) {
            request.setRole(Roles.USER.name());
        }
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLastLogin(LocalDateTime.now());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Xem danh sách người dùng thì không cần các thông tin chi tiết như booking, homestay, review
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        UserResponse response = userMapper.toUserResponse(user);
        if (user.getBookings() != null) {
            response.setBookings(user.getBookings().stream().map(Booking::getId).collect(Collectors.toSet()));
        }
        if (user.getHomestays() != null) {
            response.setHomestays(user.getHomestays().stream().map(Homestay::getId).collect(Collectors.toSet()));
        }
        if (user.getReviews() != null) {
            response.setReviews(user.getReviews().stream().map(Review::getId).collect(Collectors.toSet()));
        }
        return response;
    }

    public UserResponse updateProfile(@Valid UpdateUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateToUser(user, request);
        userRepository.save(user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateAvatar(MultipartFile avatar) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Image oldAvatar = user.getAvatar();
        if (oldAvatar != null) {
            if (!oldAvatar.getUrl().contains("google")) { // google avatar không xóa
                cloudinaryService.deleteFiles(List.of(oldAvatar.getUrl()));
            }
            oldAvatar.setUser(null);
            user.getAvatar().setUser(null);
            imageRepository.delete(oldAvatar);
        }

        String avatarUrl = cloudinaryService.uploadFile(avatar);
        Image image = Image.builder()
                .url(avatarUrl)
                .user(user)
                .build();
        user.setAvatar(image);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public String updateStatus(String email, String status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(status);
        userRepository.save(user);
        return status.toLowerCase() + " user successfully";
    }

    public String updateFavoriteHomestay(String homestayId) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getFavoriteHomestays().isEmpty()) {
            user.setFavoriteHomestays(new HashSet<>());
            user.getFavoriteHomestays().add(homestayRepository.findById(homestayId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND)));
        } else {
            if (user.getFavoriteHomestays().stream().anyMatch(homestay -> homestay.getId().equals(homestayId))) {
                user.getFavoriteHomestays().remove(homestayRepository.findById(homestayId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND)));
            } else {
                user.getFavoriteHomestays().add(homestayRepository.findById(homestayId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND)));
            }
        }
        userRepository.save(user);
        return "Update favorite homestay successfully";
    }

    public ReviewResponse createReview(String homestayId, ReviewRequest request) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HOMESTAY_NOT_FOUND));
        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .homestay(homestay)
                .build();
        user.getReviews().add(review);
        homestay.getReviews().add(review);
        userRepository.save(user);
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .userName(user.getFullName())
                .avatar(user.getAvatar().getUrl())
                .homestayId(homestay.getId())
                .build();
    }
}
