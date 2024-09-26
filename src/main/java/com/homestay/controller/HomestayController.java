package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.service.HomestayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/homestays")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HomestayController {
    HomestayService homestayService;

    // Chỉ có chủ nhà mới được phép tạo homestay, homestay đó thuộc sở hữu của chủ nhà
    @PostMapping
    @PreAuthorize("hasAuthority('LANDLORD:CREATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> createHomestay(@Valid @RequestBody HomestayRequest request) {
        return ApiResponse.<HomestayResponse>builder()
                .code(200)
                .message("Success")
                .result(homestayService.createHomestay(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN:READ_ALL_HOMESTAY')")
    public ApiResponse<List<HomestayResponse>> getAllHomestays() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .code(200)
                .message("Success")
                .result(homestayService.getAllHomestays())
                .build();
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('LANDLORD:READ_OWN_HOMESTAY') and isAuthenticated()")
    public ApiResponse<List<HomestayResponse>> getHomestayByOwner() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .code(200)
                .message("Success")
                .result(homestayService.getHomestayByOwner())
                .build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayInfo(@Valid @RequestBody HomestayRequest request) {
        return ApiResponse.<HomestayResponse>builder()
                .code(200)
                .message("Success")
                .result(homestayService.updateHomestay(request))
                .build();
    }

    @PutMapping("/photo/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayPhoto(@RequestBody List<MultipartFile> photo, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .code(200)
                .message("Success")
                .result(homestayService.updateHomestayPhoto(photo, id))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:DELETE_HOMESTAY')")
    public ApiResponse<String> deleteHomestay(@PathVariable String id) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Success")
                .result(homestayService.deleteHomestay(id))
                .build();
    }

}