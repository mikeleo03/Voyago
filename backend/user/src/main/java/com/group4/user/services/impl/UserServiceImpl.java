package com.group4.user.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.group4.user.data.model.Status;
import com.group4.user.data.model.User;
import com.group4.user.data.repository.UserRepository;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;
import com.group4.user.mapper.UserMapper;
import com.group4.user.services.UserService;

import jakarta.validation.Valid;

import com.group4.user.exceptions.ResourceNotFoundException;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "User not found";

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

    // [-][Indirect] Create a new user.
    // Assumption : password sent to this service is already encrypted.
    @Override
    @Transactional
    public UserDTO createUser(@Valid UserSaveDTO userSaveDTO) {
        User user = userMapper.toUser(userSaveDTO);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    // [User, Admin] Update existing user.
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
    @Transactional
    public UserDTO updateUserStatus(String id, Status status) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        existingUser.setStatus(status.toString());
        userRepository.save(existingUser);

        return userMapper.toUserDTO(existingUser);
    }

    // [-][Indirect] Update existing user's password.
    // Assumption : password sent to this service is already encrypted.
    @Override
    @Transactional
    public UserDTO updatePassword(String id, String newPassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        userRepository.save(existingUser);
        
        return userMapper.toUserDTO(existingUser);
    }
}
