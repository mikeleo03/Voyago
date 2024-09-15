package com.group4.authentication.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.group4.authentication.data.repository.ApiKeyRepository;
import com.group4.authentication.data.model.ApiKey;
import com.group4.authentication.services.ApiKeyService;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    @Autowired
    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    /**
     * Validates if the provided API key is valid and active.
     *
     * @param requestApiKey The API key to be validated.
     * @return {@code true} if the provided API key is valid and active, {@code false} otherwise.
     */
    @Override
    public boolean isValidApiKey(String requestApiKey) {
        // Check from the repo
        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findFirstByActiveTrueOrderById();
        if (apiKeyOpt.isPresent()) {
            // Check if it's the same
            String storedApiKey = apiKeyOpt.get().getTheKey();
            return storedApiKey.equals(requestApiKey);
        }
        return false;
    }

    @Override
    public ApiKey createApiKey(String description) {
        ApiKey apiKey = new ApiKey();
        apiKey.setTheKey(UUID.randomUUID().toString());
        apiKey.setDescription(description);
        apiKey.setActive(true);
        return apiKeyRepository.save(apiKey);
    }
}
