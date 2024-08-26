package com.homestay.service;

import com.homestay.dto.request.UserRequest;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public User createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Thêm vai trò Client cho người dùng mới
        Set<Role> roles = new HashSet<>();
        Set<Role> finalRoles = roles;
        roles = roleRepository.findById(Roles.CLIENT.name())
                .map(role -> {
                    finalRoles.add(role);
                    return finalRoles;
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dob(LocalDate.parse(request.getDob()))
                .gender(request.getGender())
                .address(request.getAddress())
                .roles(roles)
                .build();

        return userRepository.save(user);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return userRepository.findById(id).get();
    }


    public User updateUser(String id, @Valid UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDob(LocalDate.parse(request.getDob()));
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());

        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findById(roleName)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
            roles.add(role);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }
}
