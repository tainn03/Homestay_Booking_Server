package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.ChangeDiscountValueRequest;
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
    public ApiResponse<HomestayResponse> createHomestay(@RequestBody HomestayRequest request) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.createHomestay(request))
                .build();
    }

    @PutMapping("/images/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayImages(@RequestBody List<MultipartFile> images, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestayImages(images, id))
                .build();
    }

    @PutMapping("/images/new/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> addNewHomestayImages(@RequestBody List<MultipartFile> images, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.addNewHomestayImages(images, id))
                .build();
    }

    @DeleteMapping("/images/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> deleteHomestayImages(@RequestParam List<String> images, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                
                .result(homestayService.deleteHomestayImages(images, id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN:READ_ALL_HOMESTAY')")
    public ApiResponse<List<HomestayResponse>> getAllHomestays() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.getAllHomestays())
                .build();
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('LANDLORD:READ_OWN_HOMESTAY') and isAuthenticated()")
    public ApiResponse<List<HomestayResponse>> getHomestayByOwner() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.getHomestayByOwner())
                .build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayInfo(@Valid @RequestBody HomestayRequest request) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestay(request))
                .build();
    }

    @PutMapping("/photo/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayPhoto(@RequestBody List<MultipartFile> photo, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestayPhoto(photo, id))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:DELETE_HOMESTAY')")
    public ApiResponse<String> deleteHomestay(@PathVariable String id) {
        return ApiResponse.<String>builder()
                .result(homestayService.deleteHomestay(id))
                .build();
    }

    @GetMapping("/public/search")
    public ApiResponse<List<HomestayResponse>> searchHomestay(@RequestParam String transcript) {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.searchHomestays(transcript))
                .build();
    }

    @GetMapping("/public/{id}")
    public ApiResponse<HomestayResponse> getHomestayById(@PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.getHomestayById(id))
                .build();
    }

    @PutMapping("/price/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayPrice(@RequestBody double price, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestayPrice(price, id))
                .build();
    }

    @PutMapping("/weekendPrice/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayWeekendPrice(@RequestBody double weekendPrice, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestayWeekendPrice(weekendPrice, id))
                .build();
    }

    @PutMapping("/discount/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayDiscount(@RequestBody ChangeDiscountValueRequest request, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestayDiscount(request, id))
                .build();
    }

}