package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
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

}
