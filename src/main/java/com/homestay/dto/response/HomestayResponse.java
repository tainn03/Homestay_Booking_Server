package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
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
    List<String> urlImages;

    Double longitude;
    Double latitude;
    String addressDetail;

    List<RoomResponse> rooms;
    Set<String> reviewIds;
    Set<String> typeHomestayNames;
    String districtName;
    String cityName;
}
