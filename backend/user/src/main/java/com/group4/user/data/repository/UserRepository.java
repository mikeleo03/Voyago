package com.group4.user.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.group4.user.data.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    // Is the username already taken
    boolean existsByUsername(String username);
}

