package com.group4.user.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.group4.user.client.AuthClient;
import com.group4.user.data.model.User;
import com.group4.user.data.repository.UserRepository;
import com.group4.user.dto.SignupRequest;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;
import com.group4.user.exceptions.ResourceNotFoundException;
import com.group4.user.mapper.UserMapper;
import com.group4.user.services.UserService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthClient authClient;

    private static final String USER_NOT_FOUND = "User not found";

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthClient authClient) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authClient = authClient;
    }

    // [Admin] Retrieves a paginated list of all Users.
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // [-] Create a new user. Do signup
    @Override
    @Transactional
    public Mono<ResponseEntity<Map<String, String>>> signup(@Valid UserSaveDTO userSaveDTO) {
        log.info("Processing signup for username: {}", userSaveDTO.getUsername());

        if (userRepository.existsByUsername(userSaveDTO.getUsername())) {
            log.error("Signup failed: Username already taken for {}", userSaveDTO.getUsername());
            throw new IllegalArgumentException("Username is already taken.");
        }

        User user = userMapper.toUser(userSaveDTO);
        user.setPassword(passwordEncoder.encode(userSaveDTO.getPassword())); // Ensure password is encoded
        userRepository.save(user);
        log.info("Signup successful for username: {}, trying to send data to AuthService", userSaveDTO.getUsername());

        // Use the mapper to convert UserSaveDTO to SignupRequest
        userSaveDTO.setPassword(user.getPassword());
        SignupRequest signupRequest = userMapper.toSignupRequest(userSaveDTO);

        // Call the AuthClient to send the signup data
        return authClient.sendSignInData(signupRequest)
                .map(isSuccess -> {
                    if (Boolean.TRUE.equals(isSuccess)) {
                        // Successful sign-in data transfer, return success response
                        Map<String, String> response = new HashMap<>();
                        response.put("status", "Sign in successful");
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    } else {
                        // If something went wrong, handle accordingly
                        Map<String, String> response = new HashMap<>();
                        response.put("status", "Sign in failed");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                })
                .onErrorResume(ex -> {
                    log.error("Error during signup process: {}", ex.getMessage());
                    // Return the error message as JSON
                    Map<String, String> response = new HashMap<>();
                    response.put("error", ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                });
    }

    // [Customer, Admin] Update existing user.
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Transactional
    public UserDTO updateUser(String id, @Valid UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setPhone(userUpdateDTO.getPhone());
        existingUser.setPicture(userUpdateDTO.getPicture());
        userRepository.save(existingUser);

        return userMapper.toUserDTO(existingUser);
    }

    // [Admin] Updates the status of an existing user.
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO updateUserStatus(String id, String status) {
        log.info("Validating status: {}", status);
        if (!"ACTIVE".equalsIgnoreCase(status) && !"INACTIVE".equalsIgnoreCase(status)) {
            log.error("Invalid status value: {}", status);
            throw new IllegalArgumentException("Invalid status. Allowed values are 'ACTIVE' and 'INACTIVE'.");
        }
    
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        log.info("Fetched user: {}", existingUser.getUsername());
    
        if (status.equalsIgnoreCase(existingUser.getStatus())) {
            log.error("Status is already {}", existingUser.getStatus());
            throw new IllegalArgumentException("The status is already " + existingUser.getStatus() + ".");
        }
    
        existingUser.setStatus(status.toUpperCase());
        log.info("Saving user with updated status");
        log.info("User data: {}", existingUser);
        userRepository.save(existingUser);
        log.info("User saved successfully");
    
        return userMapper.toUserDTO(existingUser);
    }    

    // [Customer, Admin] Update existing user's password.
    // Assumption: password sent to this service is already encrypted.
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Transactional
    public UserDTO updatePassword(String id, String newPassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        // Assumption: password is already encrypted before this point.
        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);

        return userMapper.toUserDTO(existingUser);
    }
}
