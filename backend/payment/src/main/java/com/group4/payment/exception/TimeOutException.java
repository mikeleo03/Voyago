package com.group4.payment.exception;

public class TimeOutException extends RuntimeException{
    public TimeOutException(String message) {
        super(message);
    }
}
