package com.group4.authentication.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.group4.authentication.data.model.User;
import com.group4.authentication.data.repository.UserRepository;
import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;
import com.group4.authentication.mapper.UserMapper;
import com.group4.authentication.services.AuthService;
import com.group4.authentication.services.TokenService;

import jakarta.validation.Valid;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, TokenService tokenService, AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @Override
    public boolean update(@Valid SignupRequest signupRequest) {
        try {
            log.info("Processing update for username: {}", signupRequest.getUsername());

            // Check if the user already exists by username
            User existingUser = userRepository.findById(signupRequest.getId()).orElse(null);

            if (existingUser != null) {
                log.info("User found with ID: {}. Updating existing user.", signupRequest.getId());

                // Update existing user fields
                existingUser.setUsername(signupRequest.getUsername());
                existingUser.setEmail(signupRequest.getEmail());
                existingUser.setPassword(signupRequest.getPassword());
                existingUser.setStatus(signupRequest.getStatus());

                userRepository.save(existingUser);
                log.info("User updated successfully for ID: {}", signupRequest.getId());
            } else {
                log.info("No user found with username: {}. Creating new user.", signupRequest.getUsername());

                // Convert SignupRequest to User and create a new entry
                log.info("Request: {}", signupRequest);
                User newUser = userMapper.toUser(signupRequest);
                log.info("Map Result: {}", newUser);
                userRepository.save(newUser);
                log.info("New user created successfully for username: {}", signupRequest.getUsername());
            }

            return true;
        } catch (Exception e) {
            log.error("Failed to update/create user for username: {}. Error: {}", signupRequest.getUsername(), e.getMessage());
            return false;
        }
    }

    @Override
    public String login(LoginRequest loginRequest) {
        log.info("Attempting login for username: {}", loginRequest.getUsername());

        // Check if the user exists and get the user's status
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // Check if the user's status is ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            log.warn("Login failed for username: {}. User is inactive.", loginRequest.getUsername());
            throw new IllegalArgumentException("Your account has been deactivated by the admin. Please contact support.");
        }

        // Proceed with authentication if the user is ACTIVE
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set the authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Login successful for username: {}. Token requested for user with roles: {}", 
                loginRequest.getUsername(), authentication.getAuthorities());

        // Generate and return the JWT token
        return tokenService.generateToken(authentication);
    }

    @Override
    public boolean validateToken(String token) {
        log.info("Validating token: {}", token);

        String username = tokenService.extractUsername(token);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("Token validation failed: User not found for username {}", username);
                return new UsernameNotFoundException("User not found");
            });

        boolean isValid = tokenService.validateToken(token, user);
        log.info("Token validation result for username {}: {}", username, isValid);

        return isValid;
    }
}
