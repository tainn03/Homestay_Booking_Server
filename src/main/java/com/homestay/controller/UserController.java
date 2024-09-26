package com.homestay.controller;

import com.homestay.constants.UserStatus;
import com.homestay.dto.ApiResponse;
import com.homestay.dto.request.UpdateUserRequest;
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

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'LANDLORD')")
public class UserController {
    UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    Environment env; // final để không thay đổi giá trị của biến sau khi khởi tạo

    // Hàm này để cho admin tạo một người dùng mới (khách hàng, chủ nhà)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN:CREATE_USER')")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN:READ_USER')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Success")
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN:READ_USER', 'USER:READ_PROFILE')")
    public ApiResponse<UserResponse> profile() {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.getProfile())
                .build();
    }

    // Cập nhật thông tin của bản thân hoặc admin cập nhật thông tin của người dùng khác
    @PutMapping("/profile")
    @PreAuthorize("#request.email == authentication.principal.username or hasAnyAuthority('ADMIN:UPDATE_USER', 'USER:UPDATE_PROFILE')")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.updateProfile(request))
                .build();
    }

    @PutMapping
    @PreAuthorize("#email == authentication.principal.username or hasAuthority('ADMIN:UPDATE_USER')")
    public ApiResponse<UserResponse> updateAvatar(@RequestBody MultipartFile avatar, String email) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.updateAvatar(avatar, email))
                .build();
    }

    // Chuyển đổi trạng thái người dùng sang delete
    @DeleteMapping("/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAuthority('ADMIN:DELETE_USER')")
    public ApiResponse<String> deleteUser(@PathVariable String email) {
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result(userService.updateStatus(email, UserStatus.DELETED.name()))
                .build();
    }

    // Hàm này để cập nhật trạng thái của user (active, inactive, delete)
    @PutMapping("{id}/status")
    public ApiResponse<?> updateUserStatus(@PathVariable String id, @RequestBody String status) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(userService.updateStatus(id, status))
                .build();
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
