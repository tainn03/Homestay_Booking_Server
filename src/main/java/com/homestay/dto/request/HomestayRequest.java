package com.homestay.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    double price;
    double weekendPrice;

    String addressDetail;
    String apartment;
    String city;
    String districtName;
    Double latitude;
    Double longitude;

    String standardCheckIn;
    String standardCheckOut;
    Set<String> amenityNames;
    Set<DiscountRequest> discounts;
    Set<String> typeHomestays;
}
