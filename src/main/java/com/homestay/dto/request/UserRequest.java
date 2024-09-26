package com.homestay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    @NotBlank(message = "INVALID_EMAIL_BLANK")
    @Email(message = "INVALID_EMAIL_PATTERN")
    String email;

    @NotBlank(message = "INVALID_PASSWORD_BLANK")
    @Size(min = 8, message = "INVALID_PASSWORD_SIZE")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "INVALID_PASSWORD_PATTERN")
    String password;

    @NotBlank(message = "INVALID_FULL_NAME_BLANK")
    String fullName;

    String phone;
    String dob; // Keep as String for validation
    String gender;
    String address;
    String cccd;
    String businessLicense;
    String nationality;
    String bankName;
    String bankNum;
    String bankUsername;
    String role;
}
