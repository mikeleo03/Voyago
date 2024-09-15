package com.group4.authentication.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group4.authentication.data.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Find user data by its username
    Optional<User> findByUsername(String username);

    // Is the username already taken
    boolean existsByUsername(String username);

    // Is the email already used
    boolean existsByEmail(String email);
}
