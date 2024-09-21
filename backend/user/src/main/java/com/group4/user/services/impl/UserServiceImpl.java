package com.group4.user.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.group4.user.client.AuthClient;
import com.group4.user.data.model.User;
import com.group4.user.data.repository.UserRepository;
import com.group4.user.dto.SignupRequest;
import com.group4.user.dto.UpdatePasswordDTO;
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
    public Page<User> getAllUsers(String name, int page, int size) {

        // Create a pageable object with the specified page, size, and sort
        Pageable pageable = PageRequest.of(page, size);

        // Apply filters and pagination
        if (name != null) {
            return userRepository.findByUsernameContaining(name, pageable);
        }

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
        signupRequest.setId(user.getId());
        signupRequest.setStatus(user.getStatus());

        // Call the AuthClient to send the signup data
        return authClient.sendUpdateData(signupRequest)
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
        log.info("Fetching user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (!id.equals(existingUser.getId()) && !hasRoleAdmin()) {
            throw new AccessDeniedException("You are not authorized to update this user.");
        }

        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setPhone(userUpdateDTO.getPhone());
        existingUser.setPicture(userUpdateDTO.getPicture());

        log.info("Updating user details for username: {}", existingUser.getUsername());
        userRepository.save(existingUser);

        // Propagate changes using the new method
        propagateUserData(existingUser);

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

        // Propagate changes using the new method
        propagateUserData(existingUser);
    
        return userMapper.toUserDTO(existingUser);
    }    

    // [Customer, Admin] Update existing user's password.
    @Override
    @Transactional
    public UserDTO updatePassword(String id, @Valid UpdatePasswordDTO newPassword) {
        log.info("Fetching user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (!id.equals(existingUser.getId()) && !hasRoleAdmin()) {
            throw new AccessDeniedException("You are not authorized to update this user.");
        }

        log.info("Updating password for username: {}", existingUser.getUsername());
        existingUser.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        userRepository.save(existingUser);

        // Propagate changes using the new method
        propagateUserData(existingUser);

        return userMapper.toUserDTO(existingUser);
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return Optional.of(userMapper.toUserDTO(user));
    }

    // This method will handle data propagation to the AuthClient and logging.
    private void propagateUserData(User user) {
        log.info("Preparing to propagate user data for username: {}", user.getUsername());

        // Convert the User entity to SignupRequest
        SignupRequest signupRequest = userMapper.toSignupRequest(userMapper.toUserSaveDTO(user));
        signupRequest.setId(user.getId());
        signupRequest.setStatus(user.getStatus());

        log.info("Propagating....: {}", signupRequest);

        // Propagate data to the AuthClient
        authClient.sendUpdateData(signupRequest)
                .doOnNext(isSuccess -> {
                    if (Boolean.TRUE.equals(isSuccess)) {
                        log.info("User data successfully propagated to AuthService for user: {}", user.getUsername());
                    } else {
                        log.warn("Failed to propagate user data to AuthService for user: {}", user.getUsername());
                    }
                })
                .onErrorResume(ex -> {
                    log.error("Error while propagating user data to AuthService: {}", ex.getMessage());
                    return Mono.empty(); // Handle or log the error appropriately.
                })
                .subscribe();
    }

    // Helper method to check if the current user has the role ADMIN
    private boolean hasRoleAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
