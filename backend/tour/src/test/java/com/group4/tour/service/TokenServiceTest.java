package com.group4.tour.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;
    private SecretKey secretKey;
    private String secretKeyString;

    @BeforeEach
    void setUp() {
        // Generate a secure key for testing
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        tokenService = new TokenService() {
            @Override
            protected SecretKey getSecretKey() {
                byte[] secretBytes = Base64.getDecoder().decode(secretKeyString);
                return Keys.hmacShaKeyFor(secretBytes);
            }
        };
    }

    @Test
    void testExtractUsername() {
        String username = "testUser";
        String token = generateMockToken(username, List.of("ROLE_USER"));

        String extractedUsername = tokenService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void testExtractRoles() {
        String username = "testUser";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        String token = generateMockToken(username, roles);

        List<String> extractedRoles = tokenService.extractRoles(token);

        assertThat(extractedRoles).containsExactlyInAnyOrderElementsOf(roles);
    }

    @Test
    void testExtractExpiration() {
        String username = "testUser";
        Date expirationDate = new Date(System.currentTimeMillis() + 10000); // 10 seconds from now
        String token = generateMockTokenWithExpiration(username, expirationDate);

        Date extractedExpiration = tokenService.extractExpiration(token);

        assertThat(extractedExpiration).isCloseTo(expirationDate, 1000); // Tolerance of 1 second
    }

    private String generateMockToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .signWith(secretKey)
                .compact();
    }

    private String generateMockTokenWithExpiration(String username, Date expiration) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }
}
