package com.homestay.service;

import com.homestay.dto.request.LoginRequest;
import com.homestay.dto.request.RegisterRequest;
import com.homestay.dto.response.AuthenticationResponse;
import com.homestay.enums.TokenType;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Token;
import com.homestay.model.User;
import com.homestay.repository.RoleRepository;
import com.homestay.repository.TokenRepository;
import com.homestay.repository.UserRepository;
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

    public AuthenticationResponse register(RegisterRequest request) {
        new User();
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleRepository.findByRoleName("USER").orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND)))
                .build();
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        new AuthenticationResponse();
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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
        response.setHeader("Refresh-Token", refreshToken);
        revokeAllUserTokens(user); // Thu hồi tất cả token cũ của user
        saveUserToken(user, jwtToken); // Lưu token mới

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        // Log the authenticated user
        System.out.println("Authenticated user: " + SecurityContextHolder.getContext().getAuthentication().getName());


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
}
