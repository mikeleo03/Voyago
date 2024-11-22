package com.group4.payment.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testAuthenticationManagerBean() {
        assertNotNull(authenticationManager, "The authenticationManager bean should not be null");
    }

    @Test
    void testSecurityFilterChainBean() {
        assertNotNull(securityFilterChain, "The securityFilterChain bean should not be null");
    }

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder, "The passwordEncoder bean should not be null");
    }
}
