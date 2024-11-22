package com.group4.authentication.services.impl;

import com.group4.authentication.data.model.User;
import com.group4.authentication.data.repository.UserRepository;
import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;
import com.group4.authentication.mapper.UserMapper;
import com.group4.authentication.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdate_CreateNewUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setId("1");
        signupRequest.setUsername("newUser");
        signupRequest.setEmail("newuser@test.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setStatus("ACTIVE");

        User newUser = new User("1", "newuser@test.com", "newUser", "Password123!", "CUSTOMER", "ACTIVE");

        when(userRepository.findById(signupRequest.getId())).thenReturn(Optional.empty());
        when(userMapper.toUser(signupRequest)).thenReturn(newUser);

        boolean result = authService.update(signupRequest);

        assertTrue(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void testUpdate_UpdateExistingUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setId("1");
        signupRequest.setUsername("updatedUser");
        signupRequest.setEmail("updateduser@test.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setStatus("ACTIVE");

        User existingUser = new User("1", "olduser@test.com", "oldUser", "OldPassword123!", "CUSTOMER", "INACTIVE");

        when(userRepository.findById(signupRequest.getId())).thenReturn(Optional.of(existingUser));

        boolean result = authService.update(signupRequest);

        assertTrue(result);
        assertEquals("updatedUser", existingUser.getUsername());
        assertEquals("updateduser@test.com", existingUser.getEmail());
        assertEquals("Password123!", existingUser.getPassword());
        assertEquals("ACTIVE", existingUser.getStatus());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdate_ExceptionThrown() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setId("1");
        signupRequest.setUsername("testUser");

        when(userRepository.findById(signupRequest.getId())).thenThrow(new RuntimeException("Database error"));

        boolean result = authService.update(signupRequest);

        assertFalse(result);
    }

    @Test
    void testLogin_Successful() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("activeUser");
        loginRequest.setPassword("Password123!");

        User user = new User("1", "activeuser@test.com", "activeUser", "Password123!", "CUSTOMER", "ACTIVE");

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getAuthorities()).thenReturn(null);  // Can set authorities if needed
        when(tokenService.generateToken(authentication)).thenReturn("testToken");

        String token = authService.login(loginRequest);

        assertNotNull(token);
        assertEquals("testToken", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(authentication);
    }

    @Test
    void testLogin_UserInactive() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("inactiveUser");
        loginRequest.setPassword("Password123!");

        User user = new User("1", "inactiveuser@test.com", "inactiveUser", "Password123!", "CUSTOMER", "INACTIVE");

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));

        assertEquals("Your account has been deactivated by the admin. Please contact support.", exception.getMessage());
    }

    @Test
    void testLogin_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("invalidUser");

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateToken_Success() {
        String token = "testToken";
        String username = "validUser";

        User user = new User("1", "validuser@test.com", "validUser", "Password123!", "CUSTOMER", "ACTIVE");

        when(tokenService.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(tokenService.validateToken(token, user)).thenReturn(true);

        boolean isValid = authService.validateToken(token);

        assertTrue(isValid);
        verify(tokenService).extractUsername(token);
        verify(tokenService).validateToken(token, user);
    }

    @Test
    void testValidateToken_UserNotFound() {
        String token = "testToken";
        String username = "invalidUser";

        when(tokenService.extractUsername(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> authService.validateToken(token));

        assertEquals("User not found", exception.getMessage());
        verify(tokenService).extractUsername(token);
    }
}
