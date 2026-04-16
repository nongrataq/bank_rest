package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class ClientException extends ServiceException {
    public ClientException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
