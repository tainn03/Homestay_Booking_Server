package com.homestay.service;

import com.homestay.constants.TokenType;
import com.homestay.dto.request.ChangePasswordRequest;
import com.homestay.dto.request.LoginRequest;
import com.homestay.dto.request.RegisterRequest;
import com.homestay.dto.response.AuthenticationResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Role;
import com.homestay.model.Token;
import com.homestay.model.User;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.TokenRepository;
import com.homestay.repository.UserRepository;
import com.homestay.service.external.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    TokenRepository tokenRepository;
    UserDetailsService userDetailsService;
    EmailService emailService;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null) {
            if (user.getStatus().equals("INACTIVE")) {
                throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
            } else if (user.getStatus().equals("ACTIVE")) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }
        user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("INACTIVE")
                .role(roleRepository.findByRoleName("USER").orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND)))
                .build();
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        String confirmationToken = jwtService.generateConfirmationToken(savedUser);
        String link = "http://localhost:8080/api/v1/auth?token=" + confirmationToken;
        String message = "Email của bạn vừa đăng ký thành công tại website của chúng tôi. Cảm ơn bạn đã đăng ký. Vui lòng nhấp vào liên kết bên dưới để kích hoạt tài khoản của bạn: ";
        emailService.sendEmail(savedUser.getEmail(), savedUser.getFullName(), link, message);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokenByUserId(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    public AuthenticationResponse authenticate(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() == null || !user.getStatus().equals("ACTIVE")) {
            throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
        }
        user.setLastLogin(LocalDateTime.now());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
        response.setHeader("Refresh-token", refreshToken);

        revokeAllUserTokens(user); // Thu hồi tất cả token cũ của user
        saveUserToken(user, jwtToken); // Lưu token mới

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Refresh-token");
        String refreshToken;
        String email;
        if (authHeader == null) {
            return;
        }
        refreshToken = authHeader;
        email = jwtService.extractUsername(refreshToken);
        if (email != null) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            if (jwtService.isValidToken(refreshToken, user)) {
                String jwtToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, jwtToken);
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
                response.setStatus(200);
            }
        }
    }

    public String changePassword(ChangePasswordRequest request, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }

    public String confirm(String token) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (jwtService.isValidToken(token, user)) {
            user.setStatus("ACTIVE");
            userRepository.save(user);
            return "Account activated successfully";
        } else
            return "Invalid token";
    }

    // Gửi mail để kích hoạt tài khoản cho chủ nhà
    public String registerLandlord(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (!user.getStatus().equals("ACTIVE")) {
            throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
        }
        String confirmationToken = jwtService.generateConfirmationToken(user);
        String link = "http://localhost:8080/api/v1/auth/confirm-landlord?token=" + confirmationToken;
        String message = "Email của bạn vừa đăng ký thành công trở thành CHỦ NHÀ - người cho thuê Homestay - tại website của chúng tôi. Cảm ơn bạn đã đăng ký. Vui lòng nhấp vào liên kết bên dưới để kích hoạt tài khoản của bạn: ";
        emailService.sendEmail(user.getEmail(), user.getFullName(), link, message);
        return "Email sent to " + email;
    }

    public String confirmLandlord(String token) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (jwtService.isValidToken(token, user)) {
            Role role = roleRepository.findByRoleName("LANDLORD").orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
            user.setRole(role);
            userRepository.save(user);
            return "Account activated successfully";
        } else
            return "Invalid token";
    }
}
