package com.group4.authentication.services;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;

import jakarta.validation.Valid;

@Service
@Validated
public interface AuthService {

    // Doing signup, get the passed data from User service
    boolean signup(@Valid SignupRequest signupRequest);

    // Doing login
    String login(LoginRequest loginRequest);

    // Validate token
    boolean validateToken(String token);
}
