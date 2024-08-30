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
    String description;
    String type;
    String status;

    String user;

    Set<String> discounts;

//    Set<Image> images;

    String address;
    Double longitude;
    Double latitude;
    String addressDetail;

    Integer guests;
    Integer bedrooms;
    Integer bathrooms;

    Set<String> rooms;

    Set<String> reviews;
}
