package com.group4.user.services;

import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    // [Admin] Retrieves a paginated list of all Users.
    Page<UserDTO> getAllUsers(Pageable pageable);

    // [-][Indirect] Create a new user.
    UserDTO createUser(@Valid UserSaveDTO userSaveDTO);

    // [Customer, Admin] Update existing user.
    UserDTO updateUser(String id, @Valid UserUpdateDTO userUpdateDTO);

    // [Admin] Updates the status of an existing user.
    UserDTO updateUserStatus(String id, String status);

    // [-][Indirect] Update existing user's password.
    UserDTO updatePassword(String id, String newPassword);
}