package com.homestay.config;

import com.cloudinary.Cloudinary;
import com.homestay.auditing.ApplicationAuditAware;
import com.homestay.exception.BusinessException;
import com.homestay.exception.ErrorCode;
import com.homestay.repository.UserRepository;
import com.pusher.rest.Pusher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableJpaAuditing
public class ApplicationConfig {
    private final UserRepository userRepository;
    @Value("${application.cloudinary.cloud-name}")
    String CLOUD_NAME;
    @Value("${application.cloudinary.api-key}")
    String API_KEY;
    @Value("${application.cloudinary.api-secret}")
    String API_SECRET;

    @Value("${application.pusher.app-id}")
    String APP_ID;
    @Value("${application.pusher.key}")
    String KEY;
    @Value("${application.pusher.secret}")
    String SECRET;
    @Value("${application.pusher.cluster}")
    String CLUSTER;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Lấy ra AuthenticationManager để sử dụng trong AuthenticationProvider
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new ApplicationAuditAware();
    }

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        return new Cloudinary(config);
    }

    @Bean
    public Pusher pusher() {
        Pusher pusher = new Pusher(APP_ID, KEY, SECRET);
        pusher.setCluster(CLUSTER);
        pusher.setEncrypted(true);
        return pusher;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("homestays", "users");
    }
}
