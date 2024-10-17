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
    INVALID_CREDENTIALS(401, "Thông tin đăng nhập không chính xác", HttpStatus.UNAUTHORIZED), // 401
    UNAUTHORIZED(403, "Không có quyền truy cập", HttpStatus.FORBIDDEN), // 403
    UNCATEGORIZED(500, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR), // 500

    INVALID_KEY(400, "Khóa không hợp lệ", HttpStatus.BAD_REQUEST), // 400
    USER_ALREADY_EXISTS(400, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST), // 400
    EMAIL_ALREADY_EXISTS(400, "Email đã được đăng ký", HttpStatus.BAD_REQUEST), // 400
    HOMESTAY_ALREADY_EXIST(400, "Homestay đã tồn tại", HttpStatus.BAD_REQUEST), // 400
    NO_IMAGES_PROVIDED(400, "Không có ảnh nào được cung cấp", HttpStatus.BAD_REQUEST), // 400

    ROLE_NOT_FOUND(404, "Vai trò không tồn tại", HttpStatus.NOT_FOUND), // 404
    USER_NOT_FOUND(404, "Người dùng không tồn tại", HttpStatus.NOT_FOUND), // 404
    USER_NOT_ACTIVE(400, "Người dùng chưa được kích hoạt", HttpStatus.BAD_REQUEST), // 400
    HOMESTAY_NOT_FOUND(404, "Homestay không tồn tại", HttpStatus.NOT_FOUND), // 404
    DISCOUNT_NOT_FOUND(404, "Khuyến mãi không tồn tại", HttpStatus.NOT_FOUND), // 404
    CITY_NOT_FOUND(404, "Thành phố không tồn tại", HttpStatus.NOT_FOUND), // 404
    DISTRICT_NOT_FOUND(404, "Quận không tồn tại", HttpStatus.NOT_FOUND), // 404
    ROOM_NOT_FOUND(404, "Phòng không tồn tại", HttpStatus.NOT_FOUND), // 404
    REVIEW_NOT_FOUND(404, "Đánh giá không tồn tại", HttpStatus.NOT_FOUND), // 404
    BOOKING_NOT_FOUND(404, "Booking không tồn tại", HttpStatus.NOT_FOUND), // 404
    ROOM_ALREADY_EXISTS(400, "Phòng đã tồn tại", HttpStatus.BAD_REQUEST), // 400
    AMENITY_ALREADY_EXISTS(400, "Tiện ích đã tồn tại", HttpStatus.BAD_REQUEST), // 400
    AMENITY_NOT_FOUND(404, "Tiện ích không tồn tại", HttpStatus.NOT_FOUND), // 404
    TYPE_HOMESTAY_NOT_FOUND(404, "Loại homestay không tồn tại", HttpStatus.NOT_FOUND), // 404


    CHECKIN_AFTER_CHECKOUT(400, "Ngày check-in phải trước ngày check-out", HttpStatus.BAD_REQUEST), // 400
    NO_AVAILABLE_ROOMS(400, "Không còn phòng trống", HttpStatus.BAD_REQUEST), // 400

    INVALID_PASSWORD(400, "Mật khẩu không chính xác", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD_BLANK(400, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD_SIZE(400, "Mật khẩu phải có ít nhất 8 ký tự", HttpStatus.BAD_REQUEST), // 400
    INVALID_PASSWORD_PATTERN(400, "Mật khẩu phải chứa ít nhất một chữ cái viết hoa, một chữ cái viết thường và một số", HttpStatus.BAD_REQUEST), // 400
    INVALID_FULL_NAME_BLANK(400, "Họ tên không được để trống", HttpStatus.BAD_REQUEST), // 400
    INVALID_EMAIL_BLANK(400, "Email không được để trống", HttpStatus.BAD_REQUEST), // 400
    INVALID_EMAIL_PATTERN(400, "Email không hợp lệ", HttpStatus.BAD_REQUEST), // 400

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
