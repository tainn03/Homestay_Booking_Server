package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID, generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    String id;
    int amount;
    LocalDate date;
    String status;
    String note;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    PaymentMethod paymentMethod;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Refund> refunds;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;
}
