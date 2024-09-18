package com.group4.user.services;

import com.group4.user.data.model.User;
import com.group4.user.dto.UpdatePasswordDTO;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface UserService {

    // [Admin] Retrieves a paginated list of all Users.
    Page<User> getAllUsers(Pageable pageable);

    // [-] Create a new user. Do signup
    public Mono<ResponseEntity<Map<String, String>>> signup(@Valid UserSaveDTO userSaveDTO);

    // [Customer, Admin] Update existing user.
    UserDTO updateUser(String id, @Valid UserUpdateDTO userUpdateDTO);

    // [Admin] Updates the status of an existing user.
    UserDTO updateUserStatus(String id, String status);

    // [Customer, Admin] Update existing user's password.
    UserDTO updatePassword(String id, @Valid UpdatePasswordDTO newPassword);
}