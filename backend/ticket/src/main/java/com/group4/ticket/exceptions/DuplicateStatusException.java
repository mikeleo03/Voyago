package com.group4.ticket.exceptions;

public class DuplicateStatusException extends RuntimeException {
    public DuplicateStatusException(String message) {
        super(message);
    }
}