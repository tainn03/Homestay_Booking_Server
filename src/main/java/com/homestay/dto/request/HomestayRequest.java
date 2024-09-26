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

    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "INVALID_CHECKIN_PATTERN")
    String standardCheckIn;

    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "INVALID_CHECKOUT_PATTERN")
    String standardCheckOut;

    String phone;
    double price;
    Double longitude;
    Double latitude;
    String addressDetail;

    Set<String> typeHomestays;
    String districtName;
}
