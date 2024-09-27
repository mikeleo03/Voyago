package com.group4.payment.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();

        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        String secretBase64 = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());
        ReflectionTestUtils.setField(tokenService, "secret", secretBase64);
    }

    @Test
    void testExtractUsername() {
        String token = generateToken("testuser", List.of("ROLE_USER", "ROLE_ADMIN"));
        String username = tokenService.extractUsername(token);
        assertEquals("testuser", username, "Username should be 'testuser'");
    }

    @Test
    void testExtractRoles() {
        String token = generateToken("testuser", List.of("ROLE_USER", "ROLE_ADMIN"));
        List<String> roles = tokenService.extractRoles(token);
        assertNotNull(roles, "Roles should not be null");
        assertEquals(2, roles.size(), "There should be 2 roles");
        assertTrue(roles.contains("ROLE_USER"), "Roles should contain 'ROLE_USER'");
        assertTrue(roles.contains("ROLE_ADMIN"), "Roles should contain 'ROLE_ADMIN'");
    }

    @Test
    void testExtractExpiration() {
        Date expirationDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        String token = generateTokenWithExpiration("testuser", expirationDate);
        Date extractedExpiration = tokenService.extractExpiration(token);

        assertNotNull(extractedExpiration, "Expiration date should not be null");

        long timeDifference = Math.abs(expirationDate.getTime() - extractedExpiration.getTime());
        assertTrue(timeDifference < 1000, "Expiration date should match within 1 second");
    }

    private String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .signWith(secretKey)
                .compact();
    }

    private String generateTokenWithExpiration(String username, Date expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}
