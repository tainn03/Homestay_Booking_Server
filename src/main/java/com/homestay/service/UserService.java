package com.homestay.service;

import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.RoleResponse;
import com.homestay.dto.response.UserResponse;
import com.homestay.enums.Roles;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Role;
import com.homestay.model.User;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Thêm vai trò Client cho người dùng mới
        Set<Role> roles = new HashSet<>();
        Role clientRole = roleRepository.findById(Roles.CLIENT.name())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
        roles.add(clientRole);

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .status("ACTIVE")
                .email(request.getEmail())
                .phone(request.getPhone())
                .dob(LocalDate.parse(request.getDob()))
                .gender(request.getGender())
                .address(request.getAddress())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        Set<RoleResponse> roleResponses = savedUser.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .password(savedUser.getPassword())
                .fullName(savedUser.getFullName())
                .status(savedUser.getStatus())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .dob(savedUser.getDob())
                .gender(savedUser.getGender())
                .address(savedUser.getAddress())
                .roles(roleResponses)
                .build();
    }


    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .fullName(user.getFullName())
                        .status(user.getStatus())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .dob(user.getDob())
                        .gender(user.getGender())
                        .address(user.getAddress())
                        .roles(user.getRoles().stream()
                                .map(role -> RoleResponse.builder()
                                        .name(role.getName())
                                        .description(role.getDescription())
                                        .build())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .gender(user.getGender())
                .address(user.getAddress())
                .roles(roleResponses)
                .build();
    }

    public UserResponse updateUser(String id, @Valid UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setStatus(request.getStatus());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDob(LocalDate.parse(request.getDob()));
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findById(roleName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        User updatedUser = userRepository.save(user);

        Set<RoleResponse> roleResponses = updatedUser.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .password(updatedUser.getPassword())
                .fullName(updatedUser.getFullName())
                .status(updatedUser.getStatus())
                .email(updatedUser.getEmail())
                .phone(updatedUser.getPhone())
                .dob(updatedUser.getDob())
                .gender(updatedUser.getGender())
                .address(updatedUser.getAddress())
                .roles(roleResponses)
                .build();
    }

    public UserResponse deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);

        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .name(role.getName())
                        .description(role.getDescription())
                        .build())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .gender(user.getGender())
                .address(user.getAddress())
                .roles(roleResponses)
                .build();
    }
}
