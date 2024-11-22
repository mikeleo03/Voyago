package com.group4.authentication.services.impl;

import com.group4.authentication.data.model.ApiKey;
import com.group4.authentication.data.repository.ApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiKeyServiceImplTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsValidApiKey_ValidKey() {
        String validKey = "test-valid-key";
        ApiKey apiKey = new ApiKey(UUID.randomUUID().toString(), validKey, "Test API Key", true);

        when(apiKeyRepository.findFirstByActiveTrueOrderById()).thenReturn(Optional.of(apiKey));

        boolean result = apiKeyService.isValidApiKey(validKey);

        assertTrue(result);
        verify(apiKeyRepository, times(1)).findFirstByActiveTrueOrderById();
    }

    @Test
    void testIsValidApiKey_InvalidKey() {
        String validKey = "test-valid-key";
        String invalidKey = "test-invalid-key";
        ApiKey apiKey = new ApiKey(UUID.randomUUID().toString(), validKey, "Test API Key", true);

        when(apiKeyRepository.findFirstByActiveTrueOrderById()).thenReturn(Optional.of(apiKey));

        boolean result = apiKeyService.isValidApiKey(invalidKey);

        assertFalse(result);
        verify(apiKeyRepository, times(1)).findFirstByActiveTrueOrderById();
    }

    @Test
    void testIsValidApiKey_NoActiveKeyFound() {
        when(apiKeyRepository.findFirstByActiveTrueOrderById()).thenReturn(Optional.empty());

        boolean result = apiKeyService.isValidApiKey("any-key");

        assertFalse(result);
        verify(apiKeyRepository, times(1)).findFirstByActiveTrueOrderById();
    }

    @Test
    void testCreateApiKey_Success() {
        String description = "New API Key for testing";
        ArgumentCaptor<ApiKey> apiKeyCaptor = ArgumentCaptor.forClass(ApiKey.class);

        ApiKey savedApiKey = new ApiKey(UUID.randomUUID().toString(), "generated-key", description, true);
        when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(savedApiKey);

        ApiKey result = apiKeyService.createApiKey(description);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertTrue(result.isActive());
        assertNotNull(result.getTheKey());

        verify(apiKeyRepository).save(apiKeyCaptor.capture());
        ApiKey capturedApiKey = apiKeyCaptor.getValue();

        assertEquals(description, capturedApiKey.getDescription());
        assertTrue(capturedApiKey.isActive());
        assertNotNull(capturedApiKey.getTheKey());
    }
}
