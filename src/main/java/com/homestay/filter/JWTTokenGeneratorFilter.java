package com.homestay.filter;

import com.homestay.constants.ApplicationConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

// Filter này sẽ được kích hoạt khi người dùng đăng nhập thành công và tạo token JWT từ thông tin xác thực
public class JWTTokenGeneratorFilter extends OncePerRequestFilter { // Chỉ tạo token JWT một lần cho request login đầu tiên

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Lấy thông tin xác thực từ SecurityContextHolder để tạo token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Environment env = getEnvironment(); // Lấy thông tin environment từ Spring để lấy secret key
            if (env != null) {
                // Tạo secret key để ký JWT token
                String secret = env.getProperty(ApplicationConstant.JWT_SECRET_KEY, ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                // Tạo JWT token từ thông tin xác thực và secret key
                String jwt = Jwts.builder()
                        .issuer("homestay") // Tên ứng dụng
                        .subject("JWT Token") // Loại token (JWT)
                        .claim("username", authentication.getName()) // Thông tin người dùng
                        .claim("authorities", authentication.getAuthorities().stream() // Danh sách quyền của người dùng
                                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new Date()) // Thời gian phát hành token
                        .expiration(new Date((new Date()).getTime() + 30000000)) // Thời gian hết hạn token: 30000000ms = 8h20p
                        .signWith(secretKey) // Ký token bằng secret key
                        .compact(); // Tạo token JWT từ các thông tin trên và trả về dạng chuỗi String

                // Thêm token vào header của response
                response.setHeader(ApplicationConstant.JWT_HEADER, jwt);
            }
        }
        filterChain.doFilter(request, response); // Chuyển request và response tới filter tiếp theo
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/users/login");
        // Filter này chỉ được kích hoạt khi request đến /user, tức là khi người dùng đăng nhập.
    }
}