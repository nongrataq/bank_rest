package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ServiceException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
