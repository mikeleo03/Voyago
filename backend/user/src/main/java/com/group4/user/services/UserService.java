package com.group4.user.services;

import com.group4.user.data.model.User;
import com.group4.user.dto.UpdatePasswordDTO;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    // [Admin] Retrieves a paginated list of all Users.
    Page<User> getAllUsers(String name, int page, int size);

    // [-] Create a new user. Do signup
    public Mono<ResponseEntity<Map<String, String>>> signup(@Valid UserSaveDTO userSaveDTO);

    // [Customer, Admin] Update existing user.
    UserDTO updateUser(String id, @Valid UserUpdateDTO userUpdateDTO);

    // [Admin] Updates the status of an existing user.
    UserDTO updateUserStatus(String id, String status);

    // [Customer, Admin] Update existing user's password.
    UserDTO updatePassword(String id, @Valid UpdatePasswordDTO newPassword);

    // [-] Get user by email
    Optional<UserDTO> getUserByEmail(String email);

    Optional<UserDTO> getUserByUsername(String username);

    String saveImage(MultipartFile file);

    String getUserImageNameById(String id);
}