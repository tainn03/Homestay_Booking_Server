package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.response.RoleResponse;
import com.homestay.model.District;
import com.homestay.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    CityRepository cityRepository;
    DistrictRepository districtRepository;
    AmenityRepository amenityRepository;

    @GetMapping
    public ApiResponse<?> getAllTypeHomestay() {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(typeHomestayRepository.findAll())
                .build();
    }

    // Lấy tất cả các type homestay ngoại trừ 3 loại type homestay là "Được ưa chuộng", "Mới", "Thật ấn tượng"
    @GetMapping("/type-homestays")
    public ApiResponse<?> getAllTypeHomestayExceptPopularNewImpressive() {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(typeHomestayRepository.findAllExceptPopularNewImpressive())
                .build();
    }

    @GetMapping("/amenities")
    public ApiResponse<?> getAllAmenity() {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(amenityRepository.findAll())
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

    @GetMapping("/cities")
    public ApiResponse<?> getAllCities() {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(cityRepository.findAll())
                .build();
    }

    @GetMapping("/districts")
    public ApiResponse<List<District>> getDistrictsByCityName(@RequestParam String cityName) {
        return ApiResponse.<List<District>>builder()
                .code(200)
                .message("Success")
                .result(districtRepository.findByCityName(cityName))
                .build();
    }
}
