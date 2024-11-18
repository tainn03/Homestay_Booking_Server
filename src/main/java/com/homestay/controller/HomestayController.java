package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.ChangeDiscountValueRequest;
import com.homestay.dto.request.DiscountRequest;
import com.homestay.dto.request.HomestayRequest;
import com.homestay.dto.response.HomestayResponse;
import com.homestay.dto.response.UserResponse;
import com.homestay.model.Discount;
import com.homestay.service.HomestayService;
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
        return
                ApiResponse.<HomestayResponse>builder()
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

    @GetMapping("/public/all")
    public ApiResponse<List<HomestayResponse>> getAllHomestays() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.getAllHomestays())
                .build();
    }

    @GetMapping("/public/type/{type}")
    public ApiResponse<List<HomestayResponse>> getHomestayByType(@PathVariable String type) {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.getHomestayByType(type))
                .build();
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('LANDLORD:READ_OWN_HOMESTAY') and isAuthenticated()")
    public ApiResponse<List<HomestayResponse>> getHomestayByOwner() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.getHomestayByOwner())
                .build();
    }

    @PutMapping("/info")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestayInfo(@RequestBody HomestayRequest request) {
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
    public ApiResponse<List<HomestayResponse>> searchHomestay(@RequestParam String transcript, @RequestParam(required = false) String filter) {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.searchHomestays(transcript, filter))
                .build();
    }

    @GetMapping("/public/{id}")
    public ApiResponse<HomestayResponse> getHomestayById(@PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.getHomestayById(id))
                .build();
    }

    @GetMapping("/public/owner/{id}")
    public ApiResponse<UserResponse> getOwnerByHomestay(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .result(homestayService.getOwnerByHomestay(id))
                .build();
    }

    @PutMapping("/discount/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_HOMESTAY')")
    public ApiResponse<HomestayResponse> updateHomestaySystemDiscount(@RequestBody ChangeDiscountValueRequest request, @PathVariable String id) {
        return ApiResponse.<HomestayResponse>builder()
                .result(homestayService.updateHomestaySystemDiscount(request, id))
                .build();
    }

    @PostMapping("/discount/custom/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_HOMESTAY')")
    public ApiResponse<Discount> addHomestayDiscountCustom(@RequestBody DiscountRequest request, @PathVariable String id) {
        return ApiResponse.<Discount>builder()
                .result(homestayService.addHomestayDiscountCustom(request, id))
                .build();
    }

    @PutMapping("/discount/custom/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_HOMESTAY')")
    public ApiResponse<Discount> updateHomestayDiscountCustom(@RequestBody DiscountRequest request, @PathVariable String id) {
        return ApiResponse.<Discount>builder()
                .result(homestayService.updateHomestayDiscountCustom(request, id))
                .build();
    }

    @DeleteMapping("/discount/custom/{id}/{discountId}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_HOMESTAY')")
    public ApiResponse<String> deleteHomestayDiscountCustom(@PathVariable String id, @PathVariable String discountId) {
        return ApiResponse.<String>builder()
                .result(homestayService.deleteHomestayDiscountCustom(id, discountId))
                .build();
    }

    @GetMapping("/favorite")
    @PreAuthorize("hasAnyAuthority('USER:READ_FAVORITE', 'LANDLORD:READ_FAVORITE')")
    public ApiResponse<List<HomestayResponse>> getFavoriteHomestay() {
        return ApiResponse.<List<HomestayResponse>>builder()
                .result(homestayService.getFavoriteHomestay())
                .build();
    }

    @DeleteMapping
    public ApiResponse<String> deleteHomestayById(@RequestParam String id) {
        return ApiResponse.<String>builder()
                .result(homestayService.deleteHomestayById(id))
                .build();
    }
}