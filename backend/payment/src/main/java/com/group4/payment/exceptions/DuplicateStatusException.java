package com.group4.payment.exceptions;

public class DuplicateStatusException extends RuntimeException {
    public DuplicateStatusException(String message) {
        super(message);
    }
}
