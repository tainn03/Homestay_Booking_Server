package com.homestay.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    String currentPassword;
    String newPassword;
    String confirmPassword;
}
