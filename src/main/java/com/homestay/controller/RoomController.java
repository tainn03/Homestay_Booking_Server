package com.homestay.controller;

import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.RoomRequest;
import com.homestay.dto.response.RoomResponse;
import com.homestay.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class RoomController {
    @Autowired(required = true)
    RoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> response = roomService.getAllRooms();
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable String id) {
        RoomResponse response = roomService.getRoomById(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable String id, @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable String id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", "Room deleted successfully"));
    }

    @GetMapping("/homestay/{homestayId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByHomestayId(@PathVariable String homestayId) {
        List<RoomResponse> response = roomService.getRoomsByHomestayId(homestayId);
        return ResponseEntity.ok(new ApiResponse<>(1000, "Success", response));
    }
}
