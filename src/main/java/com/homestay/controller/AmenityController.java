package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.AmenityRequest;
import com.homestay.dto.response.AmenityResponse;
import com.homestay.service.AmenityService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AmenityController {
    @Autowired
    AmenityService amenityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AmenityResponse>>> getAllAmenities() {
        List<AmenityResponse> response = amenityService.getAmenities();
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AmenityResponse>> createAmenity(@RequestBody AmenityRequest amenityRequest) {
        AmenityResponse response = amenityService.createAmenity(amenityRequest);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AmenityResponse>> getAmenityById(@PathVariable String id) {
        AmenityResponse response = amenityService.getAmenityById(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AmenityResponse>> updateAmenity(@PathVariable String id, @RequestBody AmenityRequest amenityRequest) {
        AmenityResponse response = amenityService.updateAmenity(id, amenityRequest);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAmenity(@PathVariable String id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", "Amenity deleted successfully"));
    }
}
