package com.group4.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group4.user.data.model.User;
import com.group4.user.dto.EmailRequest;
import com.group4.user.dto.UpdatePasswordDTO;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;
import com.group4.user.services.EmailService;
import com.group4.user.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // [Admin] Get all users - Paginated.
    // [GET] /
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userService.getAllUsers(name, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("currentPage", users.getNumber());
        response.put("totalItems", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // [-] Create a new user, For signup purpose
    // [POST] /signup
    @PostMapping("/signup")
    public Mono<ResponseEntity<Map<String, String>>> signup(@RequestBody @Valid UserSaveDTO userSaveDTO) {
        return userService.signup(userSaveDTO);
    }

    // [Customer, Admin] Update an existing user.
    // [PUT] /:id
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    // [Admin] Update user status.
    // [PATCH] /:id/status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable String id, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("status");
        UserDTO updatedUser = userService.updateUserStatus(id, newStatus);
        return ResponseEntity.ok(updatedUser);
    }

    // [Customer, Admin] Update user password.
    // [PATCH] /:id/password
    @PatchMapping("/{id}/password")
    public ResponseEntity<UserDTO> updatePassword(@PathVariable String id, @RequestBody @Valid UpdatePasswordDTO newPassword) {
        UserDTO updatedUser = userService.updatePassword(id, newPassword);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    // [-] Get user by email
    @GetMapping("/email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        Optional<UserDTO> user = userService.getUserByEmail(email);

        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/username")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestParam String username) {
        Optional<UserDTO> user = userService.getUserByUsername(username);

        return user.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendHtmlEmail(@RequestBody @Valid EmailRequest emailRequest) throws MessagingException {
        emailService.sendHtmlEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getHtmlBody());
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "HTML Email sent successfully"));
    }
}
