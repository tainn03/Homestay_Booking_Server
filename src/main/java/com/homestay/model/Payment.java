package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    int amount;
    String transactionId;
    LocalDate date;
    String status;
    String note;
    String paymentMethod;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;
}
