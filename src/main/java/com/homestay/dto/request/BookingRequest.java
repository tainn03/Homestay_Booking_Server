package com.homestay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest {

    @NotBlank(message = "INVALID_CHECKIN_BLANK")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "INVALID_CHECKIN_PATTERN")
    String checkIn;

    @NotBlank(message = "INVALID_CHECKOUT_BLANK")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "INVALID_CHECKOUT_PATTERN")
    String checkOut;

    String status;

    Double total;

    //    @NotBlank(message = "INVALID_GUESTS_BLANK")
    int guests;

    String note;

    @NotBlank(message = "INVALID_USER_ID_BLANK")
    String userId;


//    String roomId;

    //    @NotBlank(message = "Payment must not be blank")
    String payment;
}
