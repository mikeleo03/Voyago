package com.group4.authentication.services;

import org.springframework.stereotype.Service;

import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;

import jakarta.validation.Valid;

@Service
public interface AuthService {

    // Doing signup
    void signup(@Valid SignupRequest signupRequest);

    // Doing login
    String login(LoginRequest loginRequest);

    // Validate token
    boolean validateToken(String token);
}
