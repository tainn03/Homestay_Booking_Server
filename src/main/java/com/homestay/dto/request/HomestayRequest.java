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
public class HomestayRequest {
    String name;
    String email;
    String phone;
    String status;
    double price;
    double weekendPrice;

    String addressDetail;
    String apartment;
    String cityName;
    String districtName;
    Double latitude;
    Double longitude;

    String standardCheckIn;
    String standardCheckOut;
    Set<AmenityRequest> amenities;
    Set<DiscountRequest> discounts;
    List<RoomRequest> rooms;
    Set<String> typeHomestays;
}
