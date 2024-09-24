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
    String phone;
    String status;

    String user;

    Set<String> discounts;

//    Set<Image> images;

    Double longitude;
    Double latitude;
    String addressDetail;

    Set<String> rooms;
    Set<String> reviews;
}
