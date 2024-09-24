package com.group4.payment.exceptions;

public class TimeoutException extends RuntimeException {
    public TimeoutException(String message) {
        super(message);
    }
}

