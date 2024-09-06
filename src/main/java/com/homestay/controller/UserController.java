package com.homestay.controller;

import com.homestay.constants.ApplicationConstant;
import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.LoginRequestDTO;
import com.homestay.dto.request.UserRequest;
import com.homestay.dto.response.LoginResponseDTO;
import com.homestay.dto.response.UserResponse;
import com.homestay.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    Environment env; // final để không thay đổi giá trị của biến sau khi khởi tạo

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Success")
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable String id) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.deleteUser(id))
                .build();
    }

    @GetMapping("/{id}/bookings")
    public ApiResponse<?> getUserBookings(@PathVariable String id) {
        return null;
    }

    // Hàm này để cập nhật trạng thái của user (active, inactive)
    @PutMapping("{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable String id, @RequestBody Object status) {
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        String jwt = "";

        // Tạo thông tin xác thực từ thông tin đăng nhập
        Authentication authentication = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password());
        // Xác thực thông tin xác thực
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        // Nếu thông tin xác thực hợp lệ, tạo token JWT
        if (authenticationResponse != null && authenticationResponse.isAuthenticated()) {
            if (env != null) {
                // Tạo secret key để ký JWT token
                String secret = env.getProperty(ApplicationConstant.JWT_SECRET_KEY, ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                // Tạo JWT token từ thông tin xác thực và secret key
                jwt = Jwts.builder()
                        .issuer("eazybank") // Tên ứng dụng
                        .subject("JWT Token") // Loại token (JWT)
                        .claim("username", authenticationResponse.getName()) // Thông tin người dùng
                        .claim("authorities", authenticationResponse.getAuthorities().stream() // Danh sách quyền của người dùng
                                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new java.util.Date()) // Thời gian phát hành token
                        .expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000)) // Thời gian hết hạn token: 30000000ms = 8h20p
                        .signWith(secretKey) // Ký token bằng secret key
                        .compact(); // Tạo token JWT từ các thông tin trên và trả về dạng chuỗi String
            }
        }
        // Trả về token JWT trong header của response để client sử dụng trong các request tiếp theo
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstant.JWT_HEADER, jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));

    }
}
