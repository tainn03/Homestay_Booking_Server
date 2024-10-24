package com.homestay.controller;

import com.homestay.dto.ApiResponse;
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

    @PutMapping("/images/{nameRoom}")
    @PreAuthorize("hasAuthority('LANDLORD:UPDATE_ROOM')")
    public ApiResponse<String> createRoomImages(@RequestBody List<MultipartFile> images, @RequestParam String homestayId, @PathVariable String nameRoom) {
        return ApiResponse.<String>builder()
                .result(roomService.createRoomImages(nameRoom, images, homestayId))
                .build();
    }

}
