package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.response.RoleResponse;
import com.homestay.repository.PermissionRepository;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.TypeHomestayRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/general")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GeneralController {
    TypeHomestayRepository typeHomestayRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @GetMapping
    public ApiResponse<?> getAllTypeHomestay() {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(typeHomestayRepository.findAll())
                .build();
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .code(200)
                .message("Success")
                .result(roleRepository.findAll().stream()
                        .map(role -> RoleResponse.builder()
                                .roleName(role.getRoleName())
                                .permissions(role.getPermissions().stream()
                                        .map(permission -> permission.getPermission())
                                        .collect(Collectors.toSet()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
