package com.group4.user.data.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group4.user.data.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    // Is the username already taken
    boolean existsByUsername(String username);

    // Find user by email
    Optional<User> findByEmail(String email);

    Page<User> findByUsernameContaining(String username, Pageable pageable);
}

