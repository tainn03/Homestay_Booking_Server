package com.homestay.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.homestay.constants.TokenType;
import com.homestay.dto.request.ChangePasswordRequest;
import com.homestay.dto.request.LoginRequest;
import com.homestay.dto.request.RegisterRequest;
import com.homestay.dto.response.AuthenticationResponse;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.model.Image;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    @Value("${application.security.google.client-id}")
    String CLIENTID;
    @Value("${application.security.google.client-secret}")
    String CLIENT_SECRET;
    @Value("${application.security.google.redirect-uri}")
    String REDIRECT_URI;
    @Value("${application.utils.uppercase}")
    String UPPERCASE;
    @Value("${application.utils.lowercase}")
    String LOWERCASE;
    @Value("${application.utils.digits}")
    String DIGITS;
    @Value("${application.utils.special-chars}")
    String SPECIAL_CHARS;

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
        String message = "Cảm ơn bạn đã đăng ký tài khoản trên hệ thống của chúng tôi. Để hoàn tất quá trình đăng ký, vui lòng xác nhận tài khoản của bạn.";
        String subject = "Xác thực tài khoản của bạn";
        String buttonText = "Xác thực tài khoản";
        emailService.sendEmail(savedUser.getEmail(), savedUser.getFullName(), link, message, subject, buttonText);

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

    @Transactional
    public AuthenticationResponse authenticate(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() == null || !user.getStatus().equals("ACTIVE")) {
            throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
        }
        user.setLastLogin(LocalDateTime.now());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String jwtToken = jwtService.generateToken(user).trim();
        String refreshToken = jwtService.generateRefreshToken(user).trim();
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

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!user.getPassword().matches("google")) { // Nếu không phải mật khẩu mặc định do đăng nhập bằng Google
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
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
        String subject = "Xác thực đăng ký trở thành chủ nhà";
        String message = "Cảm ơn bạn đã đăng ký trở thành chủ nhà trên hệ thống của chúng tôi. Vui lòng xác thực tài khoản để hoàn tất quá trình đăng ký.";
        String buttonText = "Xác thực đăng ký";
        emailService.sendEmail(email, user.getFullName(), link, message, subject, buttonText);
        return "Email sent to " + email;
    }

    public void confirmLandlord(String token, HttpServletResponse response) throws IOException {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (jwtService.isValidToken(token, user)) {
            Role role = roleRepository.findByRoleName("LANDLORD").orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
            user.setRole(role);
            userRepository.save(user);
            response.sendRedirect("http://localhost:3000");
        }
    }

    public void loginGoogleAuth(HttpServletResponse response) throws IOException {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=" + REDIRECT_URI +
                "&response_type=code&client_id=" + CLIENTID +
                "&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid&access_type=offline&prompt=consent";
        response.sendRedirect(googleAuthUrl);
    }

    // Hàm lấy OAuth Access Token từ Google
    public void getOauthAccessTokenGoogle(String code, HttpServletResponse servletResponse) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("client_id", CLIENTID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";

        // Gửi yêu cầu POST đến Google để đổi code lấy token
        String response = restTemplate.postForObject(url, requestEntity, String.class);

        // Phân tích cú pháp phản hồi JSON để lấy access token
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        String accessToken = jsonObject.get("access_token").getAsString();

        // Lấy thông tin người dùng từ Google với access token
        getProfileDetailsGoogle(accessToken, servletResponse);
    }

    private void getProfileDetailsGoogle(String accessToken, HttpServletResponse servletResponse) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);  // Thiết lập access token cho Bearer Auth

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        // Phân tích cú pháp JSON để lấy thông tin người dùng
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);

        // Check and create user if not exists
        User user = checkAndCreateUser(jsonObject);

        // Tạo JWT token cho user
        String jwtToken = jwtService.generateToken(user);

        // Lưu token mới vào hệ thống
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        // Điều hướng về frontend kèm theo accessToken
        String redirectUrl = "http://localhost:3000?accessToken=" + jwtToken;
        servletResponse.sendRedirect(redirectUrl);
    }

    public User checkAndCreateUser(JsonObject userInfo) {
        String email = userInfo.get("email").getAsString();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = User.builder()
                    .fullName(userInfo.get("name").getAsString())
                    .email(email)
                    .password("google") // Mật khẩu mặc định
                    .status("ACTIVE")
                    .role(roleRepository.findByRoleName("USER").orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND)))
                    .build();
            Image image = Image.builder()
                    .url(userInfo.get("picture").getAsString())
                    .user(user)
                    .build();
            user.setAvatar(image);
            userRepository.save(user);
        } else {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
        return user;
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Gửi email với mật khẩu mới
        String subject = "Mật khẩu mới của bạn";
        String body = "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Mật khẩu mới của bạn là: " + newPassword + ". Để đảm bảo an toàn, vui lòng đổi mật khẩu sau khi đăng nhập.";
        String buttonText = "Kích hoạt mật khẩu";
        emailService.sendEmail(email, user.getFullName(), "http://localhost:3000/login", body, subject, buttonText);

        return "New password sent to " + email;
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();

        // Đảm bảo có ít nhất một ký tự của mỗi loại
        StringBuilder password = new StringBuilder();
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Thêm các ký tự ngẫu nhiên khác để đạt độ dài 8
        String allChars = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
        for (int i = 4; i < 8; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Trộn ngẫu nhiên các ký tự trong chuỗi
        List<Character> characters = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);

        StringBuilder finalPassword = new StringBuilder();
        for (char c : characters) {
            finalPassword.append(c);
        }

        return finalPassword.toString();
    }
}
