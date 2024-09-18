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
    public boolean signup(@Valid SignupRequest signupRequest) {
        try {
            log.info("Sent data: {}", signupRequest);
            User user = userMapper.toUser(signupRequest);
            log.info("Mapped data: {}", user);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("Signup failed: {}", e.getMessage());
            return false;
        }
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
