package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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
    String phone;
    String status;

    String userEmail;
    boolean isFavorite;

    @Builder.Default
    double rating = 4.0;

    Set<DiscountResponse> discounts;
    List<String> urlImages;

    Double longitude;
    Double latitude;
    String addressDetail;

    List<RoomResponse> rooms;
    Set<String> reviewIds;
    String typeHomestayName;
    String districtName;
    String cityName;
    Set<AmenityResponse> amenities;
    LocalDateTime createdAt;
}
