package com.group4.authentication.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;
import com.group4.authentication.services.AuthService;

import jakarta.validation.Valid;

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

    @PostMapping("/update")
    public ResponseEntity<Boolean> update(@RequestBody @Valid SignupRequest signupRequest) {
        log.info("Update request received for ID: {}", signupRequest.getId());
        boolean isSuccess = authService.update(signupRequest);
        log.info("Update successful for ID: {}", signupRequest.getId());
        return ResponseEntity.status(HttpStatus.OK).body(isSuccess);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("Login request received for username: {}", loginRequest.getUsername());
        String token = authService.login(loginRequest);
        log.info("Login successful for username: {}", loginRequest.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token));
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        log.info("Token validation request received");
        boolean isValid = authService.validateToken(token);
        log.info("Token validation result: {}", isValid);
        return ResponseEntity.status(HttpStatus.OK).body(isValid);
    }
}