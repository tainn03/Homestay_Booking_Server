package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.response.BookingResponse;
import com.homestay.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;

    @GetMapping("/public/{homestayId}")
    public ApiResponse<BookingResponse> getReviewBooking(@PathVariable String homestayId,
                                                         @RequestParam String checkIn,
                                                         @RequestParam String checkOut,
                                                         @RequestParam int guests) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getReviewBooking(homestayId, checkIn, checkOut, guests))
                .build();
    }

    @PostMapping("/{homestayId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<BookingResponse> createBooking(@PathVariable String homestayId,
                                                      @RequestParam String checkIn,
                                                      @RequestParam String checkOut,
                                                      @RequestParam int guests) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.createBooking(homestayId, checkIn, checkOut, guests))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<BookingResponse> getBooking(@PathVariable String id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBooking(id))
                .build();
    }
}
