package com.group4.ticket.auditor;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider",
        dateTimeProviderRef = "dateTimeProvider")
public class AuditConfig {

    private static final Logger log = LoggerFactory.getLogger(AuditConfig.class);

    @Bean
    public AuditorAware<String> auditorProvider() {
        log.info("Initializing AuditorAware Bean");
        return new SpringSecurityAuditorAware();
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        log.info("Initializing DateTimeProvider Bean");
        return () -> {
            LocalDateTime now = LocalDateTime.now();
            log.debug("Providing current LocalDateTime: {}", now);
            return Optional.of(now);
        };
    }
}