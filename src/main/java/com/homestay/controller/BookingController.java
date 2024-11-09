package com.homestay.controller;

import com.homestay.constants.BookingStatus;
import com.homestay.dto.ApiResponse;
import com.homestay.dto.response.BookingResponse;
import com.homestay.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                                                         @RequestParam int guests,
                                                         @RequestParam String roomId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.booking(homestayId, checkIn, checkOut, guests, BookingStatus.REVIEW.name(), roomId))
                .build();
    }

    @PostMapping("/{homestayId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<BookingResponse> createBooking(@PathVariable String homestayId,
                                                      @RequestParam String checkIn,
                                                      @RequestParam String checkOut,
                                                      @RequestParam int guests,
                                                      @RequestParam String roomId) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.booking(homestayId, checkIn, checkOut, guests, BookingStatus.PENDING.name(), roomId))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<BookingResponse> getBooking(@PathVariable String id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBooking(id))
                .build();
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<List<BookingResponse>> getMyBooking() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyBooking())
                .build();
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ApiResponse<List<BookingResponse>> getAllBookings() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getAllBookings())
                .build();
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<BookingResponse> updateBookingStatus(@PathVariable String id,
                                                            @RequestParam String status,
                                                            @RequestBody String reason) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updateBookingStatus(id, status, reason))
                .build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
    public ApiResponse<BookingResponse> updateBooking(@PathVariable String id,
                                                      @RequestParam String checkIn,
                                                      @RequestParam String checkOut,
                                                      @RequestParam int guests) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.updateBooking(id, checkIn, checkOut, guests))
                .build();
    }
}
