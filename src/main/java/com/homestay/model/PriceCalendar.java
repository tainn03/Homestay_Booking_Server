package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String date;
    double price;

    @ManyToOne
    @JoinColumn(name = "homestay_id", nullable = false)
    Homestay homestay;
}
