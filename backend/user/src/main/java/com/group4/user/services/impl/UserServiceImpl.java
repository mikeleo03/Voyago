package com.group4.user.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.group4.user.data.model.User;
import com.group4.user.data.repository.UserRepository;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;
import com.group4.user.exceptions.ResourceNotFoundException;
import com.group4.user.mapper.UserMapper;
import com.group4.user.services.UserService;

import jakarta.validation.Valid;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "User not found";

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    // [Admin] Retrieves a paginated list of all Users.
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                             .map(userMapper::toUserDTO);
    }

    // [Indirect] Create a new user.
    // Assumption: password sent to this service is already encrypted.
    @Override
    @Transactional
    public UserDTO createUser(@Valid UserSaveDTO userSaveDTO) {
        User user = userMapper.toUser(userSaveDTO);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
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
