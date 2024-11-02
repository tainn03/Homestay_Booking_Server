package com.homestay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;
}
