package com.group4.authentication.controller;

import com.group4.authentication.dto.LoginRequest;
import com.group4.authentication.dto.SignupRequest;
import com.group4.authentication.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testUpdate_Success() throws Exception {
        when(authService.update(any(SignupRequest.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "id": "test-id",
                            "username": "testuser",
                            "email": "test@example.com",
                            "password": "Test@123456!Momo"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authService, times(1)).update(any(SignupRequest.class));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Test@123456");

        when(authService.login(any(LoginRequest.class))).thenReturn("mocked-token");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "testuser",
                            "password": "Test@123456"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void testValidateToken_Success() throws Exception {
        when(authService.validateToken("mocked-token")).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/validateToken")
                .param("token", "mocked-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authService, times(1)).validateToken("mocked-token");
    }

    @Test
    void testValidateToken_Invalid() throws Exception {
        when(authService.validateToken("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/validateToken")
                .param("token", "invalid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(authService, times(1)).validateToken("invalid-token");
    }
}

