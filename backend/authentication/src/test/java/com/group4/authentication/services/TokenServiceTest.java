package com.group4.authentication.services;

import com.group4.authentication.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService();

        // Generate a secure key for the HS512 algorithm
        SecretKey secureKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String encodedKey = Base64.getEncoder().encodeToString(secureKey.getEncoded());
        
        // Set the secure key manually for testing
        tokenService.secret = encodedKey; 
    }

    @Test
    void testGenerateToken() {
        // Arrange
        String testUsername = "testUser";
        when(userDetails.getUsername()).thenReturn(testUsername);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        // Act
        String token = tokenService.generateToken(authentication);
        
        // Assert
        assertNotNull(token); // Ensure that a token is generated
        assertEquals(testUsername, tokenService.extractUsername(token)); // Ensure the token contains the correct username
    }

    @Test
    void testExtractUsername() {
        String token = tokenService.createToken(new HashMap<>(), "testUser");
        String username = tokenService.extractUsername(token);

        assertEquals("testUser", username);
    }

    @Test
    void testExtractExpiration() {
        String token = tokenService.createToken(new HashMap<>(), "testUser");
        Date expiration = tokenService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // The token should expire in the future
    }

    @Test
    void testValidateToken_Success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_USER")); // Add roles if needed
        String token = tokenService.createToken(claims, "testUser");
        when(user.getUsername()).thenReturn("testUser");

        assertTrue(tokenService.validateToken(token, user));
    }

    @Test
    void testValidateToken_Failure_ExpiredToken() {
        // Simulating an expired token by creating one with a very short expiration time (e.g., 1 millisecond)
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1)) // Set expiration to 1 millisecond
                .signWith(tokenService.getSecretKey(), SignatureAlgorithm.HS512)
                .compact();

        when(user.getUsername()).thenReturn("testUser");

        // Now the token should be expired, so validateToken should return false
        assertFalse(tokenService.validateToken(token, user));
    }

    @Test
    void testValidateToken_Failure_UserMismatch() {
        String token = tokenService.createToken(new HashMap<>(), "testUser");
        when(user.getUsername()).thenReturn("differentUser");

        assertFalse(tokenService.validateToken(token, user));
    }
}
