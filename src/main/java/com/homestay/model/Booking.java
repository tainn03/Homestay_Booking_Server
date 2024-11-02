package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    LocalDate checkIn;
    LocalDate checkOut;
    String status;
    String note;

    int originalTotal;
    int totalDiscount;
    int totalCost;
    int guests;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "booking_room",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    List<Room> rooms;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    Payment payment;

    @Version
    @Builder.Default
    Long version = 0L;
}
