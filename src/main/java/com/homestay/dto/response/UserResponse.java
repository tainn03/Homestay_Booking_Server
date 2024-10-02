package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String id;
    String email;
    String fullName;
    String status;
    String phone;
    LocalDate dob;
    String gender;
    String address;
    String cccd;
    String businessLicense;
    String nationality;
    String bankName;
    String bankNum;
    String bankUsername;
    String urlAvatar;
    String role;
    LocalDateTime lastLogin;

    Set<String> homestays;
    Set<String> bookings;
    Set<String> reviews;
}
