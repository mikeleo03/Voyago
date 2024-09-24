package com.group4.payment.exceptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR = "error";

    // Handle validation errors from @Valid annotated methods
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(getErrorsMap(errors), headers, HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
    
    /**
     * Handles {@link IllegalArgumentException} by creating a response entity containing an error message.
     *
     * @param e the {@link IllegalArgumentException} to handle
     * @return a {@link ResponseEntity} containing a map with an error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions by creating a response entity containing an error message.
     *
     * @param e the exception to handle
     * @return a response entity containing a map with an error message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@link IOException} by creating a response entity containing an error message.
     *
     * @param e the {@link IOException} to handle
     * @return a {@link ResponseEntity} containing a map with an error message
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleIOException(IOException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Custom exceptions
    /**
     * Handles {@link ResourceNotFoundException} by creating a response entity containing an error message.
     *
     * @param e the {@link ResourceNotFoundException} to handle
     * @return a {@link ResponseEntity} containing a map with an error message
     * @throws ResourceNotFoundException if the specified resource is not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link BadRequestException} by creating a response entity containing an error message.
     *
     * @param e the {@link BadRequestException} to handle
     * @return a {@link ResponseEntity} containing a map with an error message
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleBadRequestException(BadRequestException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link InsufficientQuantityException} by creating a response entity containing an error message.
     *
     * @param e the {@link InsufficientQuantityException} to handle
     * @return a {@link ResponseEntity} containing a map with an error message
     */
    @ExceptionHandler(InsufficientQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleInsufficientQuantityException(InsufficientQuantityException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link TimeoutException} by creating a response entity containing an error message.
     *
     * @param e the {@link TimeoutException} to handle
     * @return a {@link ResponseEntity} containing a map with an error message
     */
    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleTimeoutException(TimeoutException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}

