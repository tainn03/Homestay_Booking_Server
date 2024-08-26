package com.homestay.controller;

import com.homestay.model.Homestay;
import com.homestay.service.HomestayService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/homestays")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class HomestayController {
    HomestayService homestayService;

    @PostMapping
    public ResponseEntity<?> createHomestay(@RequestBody Homestay homestay) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAllHomestays() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHomestayById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHomestay(@PathVariable Long id, @RequestBody Homestay homestay) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHomestay(@PathVariable Long id) {
        return null;
    }

    
}
