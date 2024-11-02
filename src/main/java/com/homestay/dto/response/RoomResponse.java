package com.homestay.dto.response;

import com.homestay.model.PriceCalendar;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    String id;
    String name;
    int size;
    double price;
    double weekendPrice;
    String status;
    List<String> images;
    Set<String> bookings;
    Set<AmenityResponse> amenities;
    Set<DiscountResponse> discounts;
    Set<PriceCalendar> priceCalendars;
}

