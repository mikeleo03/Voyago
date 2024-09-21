package com.group4.tour.auditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringSecurityAuditorAwareTest {
    private SpringSecurityAuditorAware auditorAware;

    @BeforeEach
    public void setUp() {
        auditorAware = new SpringSecurityAuditorAware();
    }

    @Test
    public void testGetCurrentAuditor_WhenAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.of("user"), auditor);
    }

    @Test
    public void testGetCurrentAuditor_WhenNotAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.of("System"), auditor);
    }

    @Test
    public void testGetCurrentAuditor_WhenNoAuthentication() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        Optional<String> auditor = auditorAware.getCurrentAuditor();
        assertEquals(Optional.of("System"), auditor);
    }
}
