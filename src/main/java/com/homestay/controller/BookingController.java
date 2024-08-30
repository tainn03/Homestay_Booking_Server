package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.BookingRequest;
import com.homestay.dto.response.BookingResponse;
import com.homestay.service.BookingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {
    @Autowired
    BookingService bookingService;

    // CRUD methods: Create, Read, Update, Delete
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookings() {
        List<BookingResponse> response = bookingService.getBookings();
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(@PathVariable String id) {
        BookingResponse response = bookingService.getBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(@PathVariable String id, @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBooking(@PathVariable String id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", "Booking deleted successfully"));
    }

    // Additional methods: getBookingsByUserId, getBookingsByRoomId, updateBookingStatus
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody String status) {
        return null;
    }
}
