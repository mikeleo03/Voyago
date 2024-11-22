package com.group4.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.group4.user.data.model.User;
import com.group4.user.dto.EmailRequest;
import com.group4.user.dto.UpdatePasswordDTO;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import com.group4.user.dto.UserUpdateDTO;
import com.group4.user.exceptions.ResourceNotFoundException;
import com.group4.user.services.EmailService;
import com.group4.user.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @GetMapping("/{username}/image")
    public ResponseEntity<Resource> getUserImage(@PathVariable String username) throws MalformedURLException {
        Optional<UserDTO> user = userService.getUserByUsername(username);
        if (user.isEmpty()){
            throw new ResourceNotFoundException("User not found for this username : " + username);
        }
        String imageName = user.get().getPicture();
        
        // Get the file extension and dynamically determine the content type
        Path path = Paths.get("src/main/resources/static/assets/" + imageName);
        
        if (Files.exists(path)) {
            Resource resource = new UrlResource(path.toUri());
            String contentType;
            try {
                contentType = Files.probeContentType(path);
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // Set the actual content type dynamically
                    .body(resource);
            } catch (IOException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [Customer, Admin] Update an existing user.
    // [PUT] /:id
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<UserDTO> updateUser(
        @PathVariable String id, 
        @RequestPart("user") @Valid UserUpdateDTO userUpdateDTO,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (file != null && !file.isEmpty()) {
            String imageUrl = userService.saveImage(file);
            userUpdateDTO.setPicture(imageUrl);
        } else {
            String existingImage = userService.getUserImageNameById(id);
            userUpdateDTO.setPicture(existingImage);
        }
        
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return updatedUser != null ? ResponseEntity.status(HttpStatus.OK).body(updatedUser) : ResponseEntity.notFound().build();
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
