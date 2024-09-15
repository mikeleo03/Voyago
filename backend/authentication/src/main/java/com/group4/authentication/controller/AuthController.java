package com.group4.authentication.controller;

import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;
import com.group4.authentication.services.AuthService;

import java.util.Map;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Signup request received for username: {}", signupRequest.getUsername());
        authService.signup(signupRequest);
        log.info("Signup successful for username: {}", signupRequest.getUsername());
        return ResponseEntity.ok("Sign up success");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for username: {}", loginRequest.getUsername());
        String token = authService.login(loginRequest);
        log.info("Login successful for username: {}", loginRequest.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        log.info("Token validation request received");
        boolean isValid = authService.validateToken(token);
        log.info("Token validation result: {}", isValid);
        return ResponseEntity.ok(isValid);
    }
}