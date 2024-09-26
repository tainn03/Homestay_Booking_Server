package com.homestay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @NotBlank(message = "INVALID_EMAIL_BLANK")
    @Email(message = "INVALID_EMAIL_PATTERN")
    String email;
    @NotBlank(message = "INVALID_FULL_NAME_BLANK")
    String fullName;
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
}
