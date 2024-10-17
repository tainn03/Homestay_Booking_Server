package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @ManyToOne
    @JoinColumn(name = "homestay_id", nullable = false)
    Homestay homestay;

    // Override equals và hashCode để so sánh 2 đối tượng Discount
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Discount discount = (Discount) o;
        return Double.compare(value, discount.value) == 0 && Objects.equals(id, discount.id) && Objects.equals(type, discount.type) && Objects.equals(description, discount.description) && Objects.equals(homestay, discount.homestay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, value, type, description, homestay);
    }
}
