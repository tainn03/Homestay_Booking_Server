package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.service.HomestayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/homestays")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class HomestayController {
    @Autowired
    HomestayService homestayService;

    // CRUD methods: Create, Read, Update, Delete
    @PostMapping
    public ResponseEntity<ApiResponse<HomestayResponse>> createHomestay(@Valid @RequestBody HomestayRequest request) {
        HomestayResponse response = homestayService.createHomestay(request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HomestayResponse>>> getAllHomestays() {
        List<HomestayResponse> response = homestayService.getAllHomestays();
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomestayResponse>> getHomestayById(@PathVariable String id) {
        HomestayResponse response = homestayService.getHomestayById(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HomestayResponse>> updateHomestay(@PathVariable String id, @Valid @RequestBody HomestayRequest request) {
        HomestayResponse response = homestayService.updateHomestay(id, request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteHomestay(@PathVariable String id) {
        homestayService.deleteHomestay(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", "Homestay deleted successfully"));
    }

    // Additional methods: get homestay by name, get homestay near a location
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<HomestayResponse>>> getHomestaysByUserId(@PathVariable String userId) {
        List<HomestayResponse> response = homestayService.getHomestaysByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

}