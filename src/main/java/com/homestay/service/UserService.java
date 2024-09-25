package com.homestay.service;

import com.homestay.constants.Roles;
import com.homestay.constants.UserStatus;
import com.homestay.dto.request.UpdateUserRequest;
import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.UserResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.mapper.UserMapper;
import com.homestay.model.Image;
import com.homestay.model.Role;
import com.homestay.model.User;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Xem danh sách người dùng thì không cần các thông tin chi tiết như booking, homestay, review
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllByStatus("ACTIVE");
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
            response.setBookings(user.getBookings().stream().map(booking -> booking.getId()).collect(Collectors.toSet()));
        }
        if (user.getHomestays() != null) {
            response.setHomestays(user.getHomestays().stream().map(homestay -> homestay.getId()).collect(Collectors.toSet()));
        }
        if (user.getReviews() != null) {
            response.setReviews(user.getReviews().stream().map(review -> review.getId()).collect(Collectors.toSet()));
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

    public UserResponse updateAvatar(MultipartFile avatar, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
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
}
