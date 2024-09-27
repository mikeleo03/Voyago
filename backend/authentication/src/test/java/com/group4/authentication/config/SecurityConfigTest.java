package com.group4.authentication.config;

import static org.mockito.Mockito.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.group4.authentication.services.JpaUserDetailsService;

class SecurityConfigTest {

    @Mock
    private RsaKeyConfigProperties rsaKeyConfigProperties;

    @Mock
    private JpaUserDetailsService jpaUserDetailsService;

    @InjectMocks
    private SecurityConfig securityConfig;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Generate mock RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        publicKey = (RSAPublicKey) keyPair.getPublic();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // Mock the RsaKeyConfigProperties to return the generated keys
        when(rsaKeyConfigProperties.getPublicKey()).thenReturn(publicKey);
        when(rsaKeyConfigProperties.getPrivateKey()).thenReturn(privateKey);
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        assertThat(passwordEncoder).isNotNull();
    }

    @Test
    void testAuthManager() {
        AuthenticationManager authManager = securityConfig.authManager();
        assertThat(authManager).isNotNull();
    }

    @Test
    void testJwtDecoder() {
        when(rsaKeyConfigProperties.getPublicKey()).thenReturn(publicKey);
        JwtDecoder jwtDecoder = securityConfig.jwtDecoder();
        assertThat(jwtDecoder).isNotNull();
    }
}

