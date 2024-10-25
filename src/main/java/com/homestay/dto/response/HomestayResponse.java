package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomestayResponse {
    String id;
    String name;
    String email;
    String standardCheckIn;
    String standardCheckOut;
    double price;
    double weekendPrice;

    String phone;
    String status;

    String userEmail;

    Set<DiscountResponse> discounts;
    Set<String> urlImages;

    Double longitude;
    Double latitude;
    String addressDetail;

    Set<String> roomNames;
    Set<String> reviewIds;
    Set<String> typeHomestayNames;
    String districtName;
    String cityName;
}
