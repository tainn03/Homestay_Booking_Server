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
    String standardCheckIn;
    String standardCheckOut;
    String phone;
    double price;
    Double longitude;
    Double latitude;
    String addressDetail;

    Set<String> typeHomestays;
    String districtName;
}
