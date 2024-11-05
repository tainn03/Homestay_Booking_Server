package com.homestay.dto.response;

import com.homestay.model.Payment;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    String id;
    LocalDate checkIn;
    LocalDate checkOut;

    int totalCost;
    double originalTotal;
    int totalDiscount;
    double price;
    double weekendPrice;
    int guests;
    int nights;
    int numOfWeekend;
    int numOfWeekday;
    Set<DiscountResponse> discounts;

    String status;
    String note;
    String user;

    List<RoomResponse> rooms;
    String homestayId;
    Payment payment;
}
