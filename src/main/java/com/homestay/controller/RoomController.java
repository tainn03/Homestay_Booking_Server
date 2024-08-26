package com.homestay.controller;

import com.homestay.model.Room;
import com.homestay.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class RoomController {
    RoomService roomService;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable String id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable String id, @RequestBody Room room) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable String id) {
        return null;
    }

    @GetMapping("/availability")
    public ResponseEntity<?> checkRoomAvailability(@RequestParam String startDate, @RequestParam String endDate) {
        return null;
    }

}
