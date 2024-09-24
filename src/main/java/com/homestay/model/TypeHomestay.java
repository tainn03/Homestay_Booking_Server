package com.homestay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeHomestay extends BaseEntity {
    @Id
    String name;
    String urlImage;

    @ManyToMany(mappedBy = "typeHomestays")
    Set<Homestay> homestays;
}
