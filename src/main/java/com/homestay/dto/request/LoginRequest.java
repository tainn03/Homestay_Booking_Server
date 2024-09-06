package com.homestay.dto.request;

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
public class LoginRequest {
    @NotBlank(message = "INVALID_USERNAME_BLANK")
    @Size(min = 6, message = "INVALID_USERNAME_SIZE")
    String username;

    @NotBlank(message = "INVALID_PASSWORD_BLANK")
    @Size(min = 8, message = "INVALID_PASSWORD_SIZE")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "INVALID_PASSWORD_PATTERN")
    String password;
}
