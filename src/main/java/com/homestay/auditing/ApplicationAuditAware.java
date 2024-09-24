package com.homestay.auditing;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("Auditor username: " + authentication.getName());
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return Optional.empty();
//        }
        return Optional.of("user");
    }

}
