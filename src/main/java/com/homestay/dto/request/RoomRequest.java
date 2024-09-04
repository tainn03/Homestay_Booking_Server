package com.homestay.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "INVALID_HOMESTAY_ID_BLANK")
    String homestayId;

//    Set<String> amenities;
//
//    Set<String> bookings;
}
