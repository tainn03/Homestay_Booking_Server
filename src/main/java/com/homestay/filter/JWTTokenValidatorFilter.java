package com.homestay.filter;

import com.homestay.constants.ApplicationConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(ApplicationConstant.JWT_HEADER);
        if (jwt != null) {
            try {
                Environment env = getEnvironment();
                if (env != null) {
                    // Lấy ra secret key để giải mã JWT token
                    String secret = env.getProperty(ApplicationConstant.JWT_SECRET_KEY, ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                    if (secretKey != null) {
                        // Giải mã JWT token
//                        Claims claims = Jwts.parser().verifyWith(secretKey)
//                                .build().parseClaimsJws(jwt).getPayload();
                        Claims claims = Jwts.parser()
                                .setSigningKey(secretKey)
                                .build().parseClaimsJws(jwt).getBody();
                        String username = String.valueOf(claims.get("username"));
                        String authorities = String.valueOf(claims.get("authorities"));

                        // Tạo thông tin xác thực người dùng từ JWT token
                        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                        // Đặt thông tin người dùng vào SecurityContextHolder
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT token: " + e.getMessage());
            }
        } else {
            //Nếu không có token thì trả về lỗi 403
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        //Không cần kiểm tra token nếu request là request tới /login
        return request.getServletPath().equals("/api/users/login");
    }
}
