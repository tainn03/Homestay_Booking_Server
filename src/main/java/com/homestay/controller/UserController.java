package com.homestay.controller;

import com.homestay.constants.UserStatus;
import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.UpdateUserRequest;
import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.UserResponse;
import com.homestay.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
public class UserController {
    UserService userService;

    // Hàm này để cho admin tạo một người dùng mới (khách hàng, chủ nhà)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN:CREATE_USER')")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN:READ_USER')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Success")
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN:READ_USER', 'USER:READ_PROFILE', 'LANDLORD:READ_PROFILE')")
    public ApiResponse<UserResponse> profile() {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.getProfile())
                .build();
    }

    // Cập nhật thông tin của bản thân hoặc admin cập nhật thông tin của người dùng khác
    @PutMapping("/profile")
    @PreAuthorize("#request.email == authentication.principal.username or hasAnyAuthority('ADMIN:UPDATE_USER', 'USER:UPDATE_PROFILE')")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.updateProfile(request))
                .build();
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN:UPDATE_USER', 'USER:UPDATE_PROFILE', 'LANDLORD:UPDATE_PROFILE')")
    public ApiResponse<UserResponse> updateAvatar(@RequestBody MultipartFile avatar) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Success")
                .result(userService.updateAvatar(avatar))
                .build();
    }

    // Chuyển đổi trạng thái người dùng sang delete
    @DeleteMapping("/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAuthority('ADMIN:DELETE_USER')")
    public ApiResponse<String> deleteUser(@PathVariable String email) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Success")
                .result(userService.updateStatus(email, UserStatus.DELETED.name()))
                .build();
    }

    // Hàm này để cập nhật trạng thái của user (active, inactive, delete)
    @PutMapping("{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable String id, @RequestBody String status) {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(userService.updateStatus(id, status))
                .build();
    }
}
