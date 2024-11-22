package com.group4.authentication.services;

import com.group4.authentication.data.model.ApiKey;

public interface ApiKeyService {

    // Validates if the provided API key is valid and active
    boolean isValidApiKey(String requestApiKey);

    // Creates a new API key with the provided description
    ApiKey createApiKey(String description);
}
