package com.homestay.controller;

import com.homestay.dto.request.ChangePasswordRequest;
import com.homestay.dto.request.LoginRequest;
import com.homestay.dto.request.RegisterRequest;
import com.homestay.dto.response.AuthenticationResponse;
import com.homestay.service.AuthenticationService;
import com.homestay.service.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService service;
    CloudinaryService cloudinaryService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.authenticate(request, response));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        service.refresh(request, response);
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        String message = service.changePassword(request, connectedUser);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestBody MultipartFile request) {
//        String url = cloudinaryService.uploadFile(request);
        String url = request.getOriginalFilename();
        return ResponseEntity.ok(url);
    }
}
