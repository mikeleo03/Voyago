package com.group4.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveDTO {
    
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^\\+62\\d{9,13}$", message = "Phone number must start with +62 and contain 9 to 13 digits")
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    @Size(min = 12, message = "Password must be at least 12 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    @JsonIgnore
    private String password;
}
