package com.homestay.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomRequest {
    String name;
    int size;
    double price = 0.0;
    double weekendPrice = 0.0;
    String status;
    Set<AmenityRequest> amenities;
    Set<DiscountRequest> discounts;
    List<String> images;
}
