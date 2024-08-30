package com.homestay.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomRequest {
    @NotBlank(message = "INVALID_NAME_BLANK")
    String name;

    @Min(value = 1, message = "INVALID_PRICE_NEGATIVE")
    double price;

    @Min(value = 1, message = "INVALID_ROOM_SIZE_NEGATIVE")
    int size;

    String description;

    @NotBlank(message = "INVALID_CHECKIN_BLANK")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "INVALID_CHECKIN_PATTERN")
    String checkIn;

    @NotBlank(message = "INVALID_CHECKOUT_BLANK")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "INVALID_CHECKOUT_PATTERN")
    String checkOut;

    @NotBlank(message = "INVALID_HOMESTAY_ID_BLANK")
    String homestayId;

//    Set<String> amenities;
//
//    Set<String> bookings;
}
