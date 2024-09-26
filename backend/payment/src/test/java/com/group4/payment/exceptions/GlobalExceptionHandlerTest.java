package com.group4.payment.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleValidationErrors() {
        // Arrange
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn("Validation error");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<Map<String, List<String>>> response = exceptionHandler.handleValidationErrors(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(List.of("Validation error"), response.getBody().get("errors"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().get("error"));
    }

    @Test
    void testHandleException() {
        // Arrange
        Exception ex = new Exception("Generic error");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Generic error", response.getBody().get("error"));
    }

    @Test
    void testHandleIOException() {
        // Arrange
        IOException ex = new IOException("IO error");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIOException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("IO error", response.getBody().get("error"));
    }

    @Test
    void testHandleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleResourceNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().get("error"));
    }

    @Test
    void testHandleBadRequestException() {
        // Arrange
        BadRequestException ex = new BadRequestException("Bad request");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleBadRequestException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().get("error"));
    }

    @Test
    void testHandleInsufficientQuantityException() {
        // Arrange
        InsufficientQuantityException ex = new InsufficientQuantityException("Insufficient quantity");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInsufficientQuantityException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Insufficient quantity", response.getBody().get("error"));
    }
}
