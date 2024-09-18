package com.group4.authentication.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "User")
public class User {
    
    @Id
    @Column(name = "ID", length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    @JsonIgnore
    private String password;

    @Column(name = "role", nullable = false)
    private String role = Role.CUSTOMER.toString();

    @Column(name = "status", nullable = false)
    private String status = Status.ACTIVE.toString();
}

