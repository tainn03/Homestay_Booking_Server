package com.homestay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomestayRequest {

    @NotBlank(message = "INVALID_NAME_BLANK")
    String name;

    @NotBlank(message = "INVALID_EMAIL_BLANK")
    @Email(message = "INVALID_EMAIL_PATTERN")
    String email;

    // example: 12:00
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "INVALID_CHECKIN_PATTERN")
    String standardCheckIn;

    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "INVALID_CHECKOUT_PATTERN")
    String standardCheckOut;

    @Pattern(regexp = "^\\+?[0-9]{10,}$", message = "Phone must be valid")
    String phone;

    double price;
    String description;
    String type;

    @NotBlank(message = "INVALID_USER_ID_BLANK")
    String userId;

//    Set<Image> images;

    String address;
    Double longitude;
    Double latitude;
    String addressDetail;

    Integer guests;
    Integer bedrooms;
    Integer bathrooms;

    // just for update method
    String status;
    Set<String> discounts;
    Set<String> rooms;

    Set<String> reviews;
}
