package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "homestay")
public class Homestay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "name", insertable = false, updatable = false)
    String name;
    String email;
    String standardCheckIn;
    String standardCheckOut;
    String phone;
    String status;
    Double longitude;
    Double latitude;
    String addressDetail;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToMany(mappedBy = "favoriteHomestays", fetch = FetchType.LAZY)
    Set<User> favoriteUsers;

    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Image> images;

    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Room> rooms;

    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Discount> discounts;

    @OneToMany(mappedBy = "homestay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "name", nullable = false)
    TypeHomestay typeHomestay;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    District district;

    @Version
    @Builder.Default
    Long version = 0L;
}
