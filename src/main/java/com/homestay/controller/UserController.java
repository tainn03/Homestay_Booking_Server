package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.UserRequest;
import com.homestay.model.User;
import com.homestay.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserRequest request) {
        return ApiResponse.<User>builder()
                .code(1000)
                .message("Success")
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.<List<User>>builder()
                .code(1000)
                .message("Success")
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getUserById(@PathVariable String id) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.<User>builder()
                .code(1000)
                .message("Success")
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteUser(@PathVariable String id) {
        return null;
    }

    @GetMapping("/{id}/bookings")
    public ApiResponse<?> getUserBookings(@PathVariable String id) {
        return null;
    }

    // Hàm này để cập nhật trạng thái của user (active, inactive)
    @PutMapping("{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable String id, @RequestBody Object status) {
        return null;
    }
}
