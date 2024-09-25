package com.homestay.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid", strategy = GenerationType.UUID)
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    String id;
    String url;

    @ManyToOne
    @JoinColumn(name = "homestay_id")
    Homestay homestay;

    @ManyToOne
    @JoinColumn(name = "room_id")
    Room room;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

}
