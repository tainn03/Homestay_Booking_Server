package com.homestay.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

    String addressDetail;
    String apartment;
    String cityName;
    String districtName;
    Double latitude;
    Double longitude;

    String standardCheckIn;
    String standardCheckOut;
    List<RoomRequest> rooms;
    String typeHomestay;
}
