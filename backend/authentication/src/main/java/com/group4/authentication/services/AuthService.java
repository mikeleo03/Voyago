package com.group4.authentication.services;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;

import jakarta.validation.Valid;

@Service
@Validated
public interface AuthService {

    // Doing update user data, passed from User service
    boolean update(@Valid SignupRequest signupRequest);

    // Doing login
    String login(LoginRequest loginRequest);

    // Validate token
    boolean validateToken(String token);
}
