package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    String id;
    LocalDate checkIn;
    LocalDate checkOut;
    String status;
    Double total;
    int guests;
    String note;

    String user;

    String roomName;

    String payment;
}
