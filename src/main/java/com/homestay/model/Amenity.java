package com.homestay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Amenity {
    @Id
    String name;
    String type;

    @JsonIgnore
    @ManyToMany(mappedBy = "amenities")
    Set<Homestay> homestays;

    @Override // để so sánh 2 object amenity với nhau tránh bị lỗi khi dùng Set
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amenity amenity = (Amenity) o;
        return Objects.equals(name, amenity.name) && Objects.equals(type, amenity.type) && Objects.equals(homestays, amenity.homestays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, homestays);
    }
}
