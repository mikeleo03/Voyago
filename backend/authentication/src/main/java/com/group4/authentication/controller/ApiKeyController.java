package com.group4.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group4.authentication.services.ApiKeyService;
import com.group4.authentication.data.model.ApiKey;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    /**
     * Validates the provided API Key.
     *
     * @param key The API Key to be validated.
     *
     * @return A ResponseEntity containing true if the API Key is valid,
     *         or false if the API Key is invalid.
     */
    @Operation(summary = "Validate API Key.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API Key is valid"),
        @ApiResponse(responseCode = "401", description = "API Key is invalid")
    })
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateApiKey(@RequestParam String key) {
        boolean isValid = apiKeyService.isValidApiKey(key);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiKey> createApiKey(@RequestParam String description) {
        ApiKey apiKey = apiKeyService.createApiKey(description);
        return ResponseEntity.ok(apiKey);
    }
}

