package com.group4.payment.auditor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuditConfigTest {

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testAuditorProviderBean() {
        // Suppressing the unchecked cast warning
        @SuppressWarnings("unchecked")
        AuditorAware<String> auditorAware =
                (AuditorAware<String>) applicationContext.getBean("auditorProvider");
        assertNotNull(auditorAware, "The auditorProvider bean should not be null");
    }

    @Test
    void testDateTimeProviderBean() {
        assertNotNull(dateTimeProvider, "The dateTimeProvider bean should not be null");

        // Invoke the method to ensure the current LocalDateTime is provided
        Optional<TemporalAccessor> currentTime = dateTimeProvider.getNow();

        // Check if a value is returned
        assertTrue(currentTime.isPresent(), "The current LocalDateTime should be present");

        // Optionally, you can check if the returned time is reasonable (not in the distant past or future)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(((LocalDateTime) currentTime.get()).isBefore(now.plusSeconds(1)) &&
                        ((LocalDateTime) currentTime.get()).isAfter(now.minusSeconds(1)),
                "The provided LocalDateTime should be close to the current time");
    }
}