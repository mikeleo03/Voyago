package com.group4.user.mapper;

import com.group4.user.data.model.User;
import com.group4.user.dto.SignupRequest;
import com.group4.user.dto.UserDTO;
import com.group4.user.dto.UserSaveDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testToUserDTO() {
        // Given
        User user = new User();
        user.setId("123");
        user.setUsername("johnDoe");
        user.setEmail("john@example.com");
        user.setPhone("+6281234567890");
        user.setStatus("ACTIVE");
        user.setPicture("profile.jpg");

        // When
        UserDTO userDTO = userMapper.toUserDTO(user);

        // Then
        assertThat(userDTO.getId()).isEqualTo("123");
        assertThat(userDTO.getUsername()).isEqualTo("johnDoe");
        assertThat(userDTO.getEmail()).isEqualTo("john@example.com");
        assertThat(userDTO.getPhone()).isEqualTo("+6281234567890");
        assertThat(userDTO.getStatus()).isEqualTo("ACTIVE");
        assertThat(userDTO.getPicture()).isEqualTo("profile.jpg");
    }

    @Test
    void testToUser() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId("123");
        userDTO.setUsername("johnDoe");
        userDTO.setEmail("john@example.com");
        userDTO.setPhone("+6281234567890");
        userDTO.setStatus("ACTIVE");

        // When
        User user = userMapper.toUser(userDTO);

        // Then
        assertThat(user.getUsername()).isEqualTo("johnDoe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPhone()).isEqualTo("+6281234567890");
        assertThat(user.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void testToUserSaveDTO() {
        // Given
        User user = new User();
        user.setUsername("johnDoe");
        user.setEmail("john@example.com");
        user.setPhone("+6281234567890");
        user.setPassword("Password@123");

        // When
        UserSaveDTO userSaveDTO = userMapper.toUserSaveDTO(user);

        // Then
        assertThat(userSaveDTO.getUsername()).isEqualTo("johnDoe");
        assertThat(userSaveDTO.getEmail()).isEqualTo("john@example.com");
        assertThat(userSaveDTO.getPhone()).isEqualTo("+6281234567890");
    }

    @Test
    void testToUserFromUserSaveDTO() {
        // Given
        UserSaveDTO userSaveDTO = new UserSaveDTO();
        userSaveDTO.setUsername("johnDoe");
        userSaveDTO.setEmail("john@example.com");
        userSaveDTO.setPhone("+6281234567890");
        userSaveDTO.setPassword("Password@123");

        // When
        User user = userMapper.toUser(userSaveDTO);

        // Then
        assertThat(user.getUsername()).isEqualTo("johnDoe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPhone()).isEqualTo("+6281234567890");
    }

    @Test
    void testToSignupRequest() {
        // Given
        UserSaveDTO userSaveDTO = new UserSaveDTO();
        userSaveDTO.setUsername("johnDoe");
        userSaveDTO.setEmail("john@example.com");
        userSaveDTO.setPhone("+6281234567890");
        userSaveDTO.setPassword("Password@123");

        // When
        SignupRequest signupRequest = userMapper.toSignupRequest(userSaveDTO);

        // Then
        assertThat(signupRequest.getUsername()).isEqualTo("johnDoe");
        assertThat(signupRequest.getEmail()).isEqualTo("john@example.com");
        assertThat(signupRequest.getPassword()).isEqualTo("Password@123");
    }
}

