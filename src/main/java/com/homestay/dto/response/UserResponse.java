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
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String id;
    String username;
    //    @JsonIgnore
    String password;
    String fullName;
    String status;
    String email;
    String phone;
    LocalDate dob;

    String gender;


    String address;

    Set<RoleResponse> roles;
    Set<String> homestays;
    Set<String> bookings;
}
