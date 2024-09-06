package com.homestay.config;

import com.homestay.exception.CustomAccessDeniedHandler;
import com.homestay.exception.CustomBasicAuthenticationEntryPoint;
import com.homestay.filter.JWTTokenGeneratorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        config.addAllowedMethod("*");
                        config.addAllowedHeader("*");
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))

                .csrf((csrf) -> csrf.disable())

                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class) // Thêm filter để tạo JWT token vào filter chain
//                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class) // Thêm filter để kiểm tra JWT token vào filter chain

                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
//                        .requestMatchers((request) -> request.getServletPath().equals("/api/users/login")).permitAll()
                                .anyRequest().permitAll()
                );

        http.formLogin(withDefaults());
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean   // Hàm này dùng để kiểm tra mật khẩu có bị rò rỉ (compromised) hay không
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    // Hàm này dùng để tạo ra một AuthenticationManager để xác thực thông tin đăng nhập dựa trên username và password đã mã hóa
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        // Tạo ra một AuthenticationProvider để xác thực thông tin đăng nhập dựa trên username và password đã mã hóa
        BookingUsernamePwdAuthenticationProvider authenticationProvider =
                new BookingUsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder);

        // Tạo ra một ProviderManager để quản lý các AuthenticationProvider được sử dụng để xác thực thông tin đăng nhập
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false); // Không xóa thông tin đăng nhập sau khi xác thực
        return providerManager;
    }
}
