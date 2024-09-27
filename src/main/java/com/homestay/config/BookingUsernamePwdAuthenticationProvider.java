package com.homestay.config;

import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// Hàm này sẽ được gọi khi người dùng đăng nhập vào hệ thống và kiểm tra thông tin đăng nhập của người dùng
public class BookingUsernamePwdAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService; // Sử dụng để lấy thông tin người dùng từ database
    private final PasswordEncoder passwordEncoder;

    @Override // Hàm này sẽ được gọi khi người dùng đăng nhập vào hệ thống
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        // Kiểm tra thông tin đăng nhập của người dùng với thông tin trong database
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Kiểm tra mật khẩu đã mã hóa với mật khẩu đã mã hóa trong database
        if (!passwordEncoder.matches(pwd, userDetails.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Nếu thông tin đăng nhập không chính xác thì trả về null, ngược lại trả về thông tin đăng nhập của người dùng
        return new UsernamePasswordAuthenticationToken(username, pwd, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
