package com.group4.user.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private String secret = "Base64EncodedSecretKeyForTestingJWTTokenGeneration"; // Base64 encoded secret key

    @BeforeEach
    void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Manually inject the secret value into the tokenService object
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void testExtractUsername() {
        String token = generateToken("testUser");
        String username = tokenService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testExtractRoles() {
        String token = generateTokenWithRoles("testUser", List.of("ROLE_USER", "ROLE_ADMIN"));
        List<String> roles = tokenService.extractRoles(token);
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"), roles);
    }

    @Test
    void testExtractExpiration() {
        String token = generateTokenWithExpiration("testUser", new Date(System.currentTimeMillis() + 10000));
        Date expirationDate = tokenService.extractExpiration(token);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testExtractClaim() {
        String token = generateToken("testUser");
        String claim = tokenService.extractClaim(token, Claims::getSubject);
        assertEquals("testUser", claim);
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        return Jwts.builder()
                .setSubject(username)
                .signWith(key)
                .compact();
    }

    private String generateTokenWithRoles(String username, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }

    private String generateTokenWithExpiration(String username, Date expirationDate) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }
}
