package com.group4.authentication.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.group4.authentication.data.model.User;
import com.group4.authentication.dto.SignupRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    public void setup() {
        // Initialize the mapper
        userMapper = UserMapper.INSTANCE;
    }

    @Test
    void testToUser() {
        // Prepare the SignupRequest DTO
        SignupRequest signupRequest = SignupRequest.builder()
                .id("1234")
                .username("johnDoe")
                .email("john.doe@example.com")
                .password("Password@123")
                .status("ACTIVE")
                .build();

        // Map the SignupRequest to the User entity
        User user = userMapper.toUser(signupRequest);

        // Verify the fields
        assertEquals("1234", user.getId());
        assertEquals("johnDoe", user.getUsername());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("Password@123", user.getPassword());
        assertEquals("ACTIVE", user.getStatus());

        // Since we are ignoring the "role" in the mapping, it should default to CUSTOMER
        assertEquals("CUSTOMER", user.getRole());
    }

    @Test
    void testToUserNullRequest() {
        // Map a null SignupRequest to User
        User user = userMapper.toUser(null);

        // Verify that the result is null
        assertNull(user);
    }
}
