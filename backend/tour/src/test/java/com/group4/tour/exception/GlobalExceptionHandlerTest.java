package com.group4.tour.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Some details about the request");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse errorResponse = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals("Some details about the request", errorResponse.getDetails());
    }

    @Test
    void testHandleGlobalException() {
        // Arrange
        Exception ex = new Exception("Generic error");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Some details about the request");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleGlobalException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        GlobalExceptionHandler.ErrorResponse errorResponse = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertEquals(500, errorResponse.getStatusCode());
        assertEquals("Generic error", errorResponse.getMessage());
        assertEquals("Some details about the request", errorResponse.getDetails());
    }
}