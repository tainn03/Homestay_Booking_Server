package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    String id;
    LocalDate checkIn;
    LocalDate checkOut;

    Double total;
    double originalTotal;
    int totalDiscount;
    int guests;
    int nights;
    int numOfWeekend;
    int numOfWeekday;
    List<DiscountResponse> discounts;

    String status;
    String note;
    String user;

    List<RoomResponse> rooms;
    String homestayId;
    String payment;
}
