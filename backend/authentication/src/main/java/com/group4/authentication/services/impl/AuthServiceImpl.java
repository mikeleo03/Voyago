package com.group4.authentication.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService, AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @Override
    public void signup(@Valid SignupRequest signupRequest) {
        log.info("Processing signup for username: {}", signupRequest.getUsername());

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            log.error("Signup failed: Username already taken for {}", signupRequest.getUsername());
            throw new IllegalArgumentException("Username is already taken.");
        }

        User user = userMapper.toUser(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // Ensure password is encoded
        userRepository.save(user);

        log.info("Signup successful for username: {}", signupRequest.getUsername());
    }

    @Override
    public String login(LoginRequest loginRequest) {
        log.info("Attempting login for username: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Login successful. Token requested for user with roles: {}", authentication.getAuthorities());

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
