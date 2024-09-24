package com.group4.payment.exceptions;

public class JwtDecodingException extends RuntimeException {

    public JwtDecodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtDecodingException(String message) {
        super(message);
    }
}
