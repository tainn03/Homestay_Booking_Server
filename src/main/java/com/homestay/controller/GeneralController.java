package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.repository.TypeHomestayRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/general")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GeneralController {
    TypeHomestayRepository typeHomestayRepository;

    @GetMapping
    public ApiResponse<?> getAllTypeHomestay() {
        return ApiResponse.builder()
                .code(200)
                .message("Success")
                .result(typeHomestayRepository.findAll())
                .build();
    }
}
