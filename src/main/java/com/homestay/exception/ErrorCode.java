package com.homestay.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    INVALID_CREDENTIALS(401, "Invalid credentials", HttpStatus.UNAUTHORIZED), // 401
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN), // 403
    UNCATEGORIZED(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR), // 500

    INVALID_KEY(400, "Invalid key", HttpStatus.BAD_REQUEST), // 400
    USER_ALREADY_EXISTS(400, "User already exists", HttpStatus.BAD_REQUEST), // 400
    ROLE_NOT_FOUND(404, "Role not found", HttpStatus.NOT_FOUND),  // 404
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND), // 404
    HOMESTAY_NOT_FOUND(404, "Homestay not found", HttpStatus.NOT_FOUND), // 404
    DISCOUNT_NOT_FOUND(404, "Discount not found", HttpStatus.NOT_FOUND), // 404
    ROOM_NOT_FOUND(404, "Room not found", HttpStatus.NOT_FOUND), // 404
    REVIEW_NOT_FOUND(404, "Review not found", HttpStatus.NOT_FOUND), // 404

    INVALID_USERNAME_BLANK(400, "Username must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_USERNAME_SIZE(400, "Username must be at least 6 characters", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD_BLANK(400, "Password must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD_SIZE(400, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD_PATTERN(400, "Password must contain at least one uppercase letter, one lowercase letter, and one number", HttpStatus.BAD_REQUEST), // 400
    INVALID_FULL_NAME_BLANK(400, "Full name must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_EMAIL_BLANK(400, "Email must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_EMAIL_PATTERN(400, "Invalid email pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_PHONE_BLANK(400, "Phone must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_PHONE_PATTERN(400, "Invalid phone pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_DOB_BLANK(400, "Date of birth must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_DOB_PATTERN(400, "Invalid date of birth pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_GENDER_BLANK(400, "Gender must not be blank", HttpStatus.BAD_REQUEST), // 400

    INVALID_ROLE_NAME_BLANK(400, "Role name must not be blank", HttpStatus.BAD_REQUEST), // 400

    INVALID_NAME_BLANK(400, "Name must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_CHECKIN_PATTERN(400, "Invalid check-in pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_CHECKOUT_PATTERN(400, "Invalid check-out pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_USER_ID_BLANK(400, "User ID must not be blank", HttpStatus.BAD_REQUEST), // 400


    ;

    int code;
    String message;
    HttpStatusCode statusCode;
}
