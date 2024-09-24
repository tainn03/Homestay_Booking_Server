package com.homestay.service;

import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.UserResponse;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public UserResponse createUser(UserRequest request) {
        return null;
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
                        .role(user.getRole().getRoleName())
                        .homestays(user.getHomestays().stream()
                                .map(homestay -> homestay.getName())
                                .collect(Collectors.toSet()))
                        .bookings(user.getBookings().stream()
                                .map(booking -> booking.getId())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        return null;
    }

    public UserResponse updateUser(String id, @Valid UserRequest request) {
        return null;
    }

    public UserResponse deleteUser(String id) {
        return null;
    }
}
