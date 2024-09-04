package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    String id;

    String name;
    double price;
    int size;
    String description;

    String homestayName;

    Set<String> amenities;

    Set<String> bookings;
}

