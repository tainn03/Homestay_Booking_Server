package com.homestay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Discount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    double value;
    String type;
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "homestay_id")
    Homestay homestay;

    @Version
    @Builder.Default
    Long version = 0L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return Double.compare(value, discount.value) == 0
                && Objects.equals(type, discount.type) && Objects.equals(description, discount.description)
                && Objects.equals(startDate, discount.startDate) && Objects.equals(endDate, discount.endDate)
                && Objects.equals(room, discount.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, type, description, startDate, endDate, room);
    }
}
