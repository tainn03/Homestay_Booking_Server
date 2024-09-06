package com.homestay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @NotBlank(message = "INVALID_USERNAME_BLANK")
    @Size(min = 6, message = "INVALID_USERNAME_SIZE")
    String username;

    @NotBlank(message = "INVALID_PASSWORD_BLANK")
    @Size(min = 8, message = "INVALID_PASSWORD_SIZE")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "INVALID_PASSWORD_PATTERN")
    String password;

    @NotBlank(message = "INVALID_FULL_NAME_BLANK")
    String fullName;

    String status;

    @NotBlank(message = "INVALID_EMAIL_BLANK")
    @Email(message = "INVALID_EMAIL_PATTERN")
    String email;

    //    @NotBlank(message = "INVALID_PHONE_BLANK")
    @Pattern(regexp = "^\\+?[0-9]{10,}$", message = "INVALID_PHONE_PATTERN")
    String phone;

    //    @NotBlank(message = "INVALID_DOB_BLANK")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "INVALID_DOB_PATTERN")
    String dob; // Keep as String for validation

    //    @NotBlank(message = "INVALID_GENDER_BLANK")
    String gender;

    String address;

    Set<String> roles;
//    Set<String> homestays;
//    Set<String> bookings;
}
