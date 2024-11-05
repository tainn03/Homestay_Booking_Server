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
    double price;

    String userEmail;
    boolean isFavorite;
    double rating = 4.0;
    List<String> urlImages;

    Double longitude;
    Double latitude;
    String addressDetail;

    List<RoomResponse> rooms;
    List<ReviewResponse> reviews;
    String typeHomestayName;
    String districtName;
    String cityName;
    Set<AmenityResponse> amenities;
    LocalDateTime createdAt;
}
