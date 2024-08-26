package com.homestay.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String username;
    //    @JsonIgnore
//    String password;
    String email;
    String phone;
    LocalDate dob;

    String gender;


    String address;

    Set<String> roles;
    Set<String> homestays;
    Set<String> bookings;
}
