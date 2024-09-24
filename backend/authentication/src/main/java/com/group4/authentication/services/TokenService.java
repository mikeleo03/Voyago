package com.group4.authentication.services;

import com.group4.authentication.data.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    // Generate the secret key from the configured secret value
    private SecretKey getSecretKey() {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSecretKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())); // Collect roles as a list
        return createToken(claims, userDetails.getUsername());
    }

    public String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 mins expiration
            .signWith(getSecretKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public Boolean validateToken(String token, User userData) {
        final String username = extractUsername(token);
        return (username.equals(userData.getUsername()) && !isTokenExpired(token));
    }    
}
