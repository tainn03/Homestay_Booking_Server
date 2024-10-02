package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.ChangePasswordRequest;
import com.homestay.dto.request.LoginRequest;
import com.homestay.dto.request.RegisterRequest;
import com.homestay.dto.response.AuthenticationResponse;
import com.homestay.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService service;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("success")
                .result(service.authenticate(request, response))
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("Đăng ký tài khoản thành công")
                .result(service.register(request))
                .build();
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        service.refresh(request, response);
    }

    @PostMapping("/password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        String message = service.changePassword(request, connectedUser);
        return ApiResponse.<String>builder()
                .code(200)
                .message(message)
                .result(message)
                .build();
    }

    @GetMapping
    public ResponseEntity<String> confirm(@RequestParam String token) {
        return ResponseEntity.ok(service.confirm(token));
    }

    @PostMapping("/register-landlord")
    public ApiResponse<String> registerLandlord(@RequestParam String email) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Đăng ký tài khoản thành công")
                .result(service.registerLandlord(email))
                .build();
    }

    @GetMapping("/confirm-landlord")
    public ResponseEntity<String> confirmLandlord(@RequestParam String token) {
        return ResponseEntity.ok(service.confirmLandlord(token));
    }
}
