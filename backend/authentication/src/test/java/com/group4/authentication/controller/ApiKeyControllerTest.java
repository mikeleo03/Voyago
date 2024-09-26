package com.group4.authentication.controller;

import com.group4.authentication.data.model.ApiKey;
import com.group4.authentication.services.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApiKeyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApiKeyService apiKeyService;

    @InjectMocks
    private ApiKeyController apiKeyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(apiKeyController).build();
    }

    @Test
    void testValidateApiKey_Success() throws Exception {
        String validApiKey = "valid-api-key";

        when(apiKeyService.isValidApiKey(validApiKey)).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/validate")
                .param("key", validApiKey))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(apiKeyService, times(1)).isValidApiKey(validApiKey);
    }

    @Test
    void testValidateApiKey_Failure() throws Exception {
        String invalidApiKey = "invalid-api-key";

        when(apiKeyService.isValidApiKey(invalidApiKey)).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/validate")
                .param("key", invalidApiKey))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(apiKeyService, times(1)).isValidApiKey(invalidApiKey);
    }

    @Test
    void testCreateApiKey_Success() throws Exception {
        String description = "Test API Key";
        ApiKey apiKey = new ApiKey();
        apiKey.setTheKey("new-api-key");
        apiKey.setDescription(description);
        apiKey.setActive(true);

        when(apiKeyService.createApiKey(description)).thenReturn(apiKey);

        mockMvc.perform(post("/api/v1/auth/create")
                .param("description", description)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theKey").value("new-api-key"))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.active").value(true));

        verify(apiKeyService, times(1)).createApiKey(description);
    }
}

