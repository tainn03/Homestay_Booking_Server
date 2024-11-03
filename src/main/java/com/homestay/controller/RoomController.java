package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.ChangeDiscountValueRequest;
import com.homestay.dto.request.CustomPriceRequest;
import com.homestay.dto.request.DiscountRequest;
import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.model.Discount;
import com.homestay.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
public class RoomController {
    RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAuthority('LANDLORD:CREATE_ROOM')")
    public ApiResponse<RoomResponse> createRooms(@RequestParam String homestayId, @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.createRooms(homestayId, request))
                .build();
    }

    @PutMapping("/images/{nameRoom}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_ROOM')")
    public ApiResponse<RoomResponse> addRoomImages(@RequestBody List<MultipartFile> images, @RequestParam String homestayId, @PathVariable String nameRoom) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.addRoomImages(nameRoom, images, homestayId))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_ROOM')")
    public ApiResponse<RoomResponse> updateRoom(@PathVariable String id, @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoom(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:DELETE_ROOM')")
    public ApiResponse<String> deleteRoom(@PathVariable String id) {
        return ApiResponse.<String>builder()
                .result(roomService.deleteRoom(id))
                .build();
    }

    @PutMapping("/price/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_PRICE_ROOM')")
    public ApiResponse<RoomResponse> updateRoomPrice(@RequestBody double price, @PathVariable String id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoomPrice(price, id))
                .build();
    }

    @PutMapping("/price/week/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_PRICE_ROOM')")
    public ApiResponse<RoomResponse> updateRoomWeekendPrice(@RequestBody double weekendPrice, @PathVariable String id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoomWeekendPrice(weekendPrice, id))
                .build();
    }

    @PutMapping("/price/calendar/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_PRICE_ROOM')")
    public ApiResponse<RoomResponse> updateRoomPriceCalendar(@RequestBody List<CustomPriceRequest> requests, @PathVariable String id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoomPriceCalendar(requests, id))
                .build();
    }

    @PutMapping("/discount/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_ROOM')")
    public ApiResponse<RoomResponse> updateRoomSystemDiscount(@RequestBody ChangeDiscountValueRequest request, @PathVariable String id) {
        return ApiResponse.<RoomResponse>builder()
                .result(roomService.updateRoomSystemDiscount(request, id))
                .build();
    }

    @PostMapping("/discount/custom/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_ROOM')")
    public ApiResponse<Discount> addRoomCustomDiscount(@RequestBody DiscountRequest request, @PathVariable String id) {
        return ApiResponse.<Discount>builder()
                .result(roomService.addRoomCustomDiscount(request, id))
                .build();
    }

    @PutMapping("/discount/custom/{id}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_ROOM')")
    public ApiResponse<Discount> updateRoomCustomDiscount(@RequestBody DiscountRequest request, @PathVariable String id) {
        return ApiResponse.<Discount>builder()
                .result(roomService.updateRoomCustomDiscount(request, id))
                .build();
    }

    @DeleteMapping("/discount/custom/{id}/{discountId}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_DISCOUNT_HOMESTAY')")
    public ApiResponse<String> deleteRoomCustomDiscount(@PathVariable String id, @PathVariable String discountId) {
        return ApiResponse.<String>builder()
                .result(roomService.deleteRoomCustomDiscount(id, discountId))
                .build();
    }
}
