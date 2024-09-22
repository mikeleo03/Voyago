package com.group4.ticket.auditor;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final Logger log = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("No authentication found in SecurityContext. Defaulting to 'System'");
            return Optional.of("System"); // Fallback value
        }

        if (!authentication.isAuthenticated()) {
            log.warn("User is not authenticated. Defaulting to 'System'");
            return Optional.of("System"); // Fallback value
        }

        if (authentication.getPrincipal() instanceof String username) {
            log.info("Authenticated user found: {}", username);
            return Optional.of(username);
        } else {
            log.warn("Authentication principal is not a String. Defaulting to 'Unknown'");
            return Optional.of("Unknown");
        }
    }
}