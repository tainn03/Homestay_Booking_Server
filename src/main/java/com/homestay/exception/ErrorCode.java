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
    USER_NOT_ACTIVE(400, "User is not active", HttpStatus.BAD_REQUEST), // 400
    HOMESTAY_NOT_FOUND(404, "Homestay not found", HttpStatus.NOT_FOUND), // 404
    DISCOUNT_NOT_FOUND(404, "Discount not found", HttpStatus.NOT_FOUND), // 404
    ROOM_NOT_FOUND(404, "Room not found", HttpStatus.NOT_FOUND), // 404
    REVIEW_NOT_FOUND(404, "Review not found", HttpStatus.NOT_FOUND), // 404
    BOOKING_NOT_FOUND(404, "Booking not found", HttpStatus.NOT_FOUND), // 404
    ROOM_ALREADY_EXISTS(400, "Room already exists", HttpStatus.BAD_REQUEST), // 400
    AMENITY_ALREADY_EXISTS(400, "Amenity already exists", HttpStatus.BAD_REQUEST), // 400
    AMENITY_NOT_FOUND(404, "Amenity not found", HttpStatus.NOT_FOUND), // 404

    CHECKIN_AFTER_CHECKOUT(400, "Check-in date must be before check-out date", HttpStatus.BAD_REQUEST), // 400
    NO_AVAILABLE_ROOMS(400, "No available rooms", HttpStatus.BAD_REQUEST), // 400

    INVALID_USERNAME_BLANK(400, "Username must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD(400, "Invalid password", HttpStatus.BAD_REQUEST), // 400
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
    INVALID_CMND_PATTERN(400, "Invalid CMND pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_BUSINESS_LICENSE_PATTERN(400, "Invalid business license pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_BANK_NUM_PATTERN(400, "Invalid bank number pattern", HttpStatus.BAD_REQUEST), // 400

    INVALID_ROLE_NAME_BLANK(400, "Role name must not be blank", HttpStatus.BAD_REQUEST), // 400

    INVALID_NAME_BLANK(400, "Name must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_CHECKIN_PATTERN(400, "Invalid check-in pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_CHECKOUT_PATTERN(400, "Invalid check-out pattern", HttpStatus.BAD_REQUEST), // 400
    INVALID_USER_ID_BLANK(400, "User ID must not be blank", HttpStatus.BAD_REQUEST), // 400

    INVALID_ROOM_SIZE_BLANK(400, "Room size must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_ROOM_SIZE_NEGATIVE(400, "Room size must be greater than 0", HttpStatus.BAD_REQUEST), // 400
    INVALID_HOMESTAY_ID_BLANK(400, "Homestay ID must not be blank", HttpStatus.BAD_REQUEST), // 400

    INVALID_CHECKIN_BLANK(400, "Check-in date must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_CHECKOUT_BLANK(400, "Check-out date must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_TOTAL_BLANK(400, "Total must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_GUESTS_BLANK(400, "Guests must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_ROOM_ID_BLANK(400, "Room ID must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_PAYMENT_BLANK(400, "Payment must not be blank", HttpStatus.BAD_REQUEST), // 400
    INVALID_PRICE_NEGATIVE(400, "Price must be greater than 0", HttpStatus.BAD_REQUEST), // 400
    CHECKIN_CHECKOUT_IN_PAST(400, "Check-in and check-out dates must be in the future", HttpStatus.BAD_REQUEST), // 400


    ;

    int code;
    String message;
    HttpStatusCode statusCode;
}
