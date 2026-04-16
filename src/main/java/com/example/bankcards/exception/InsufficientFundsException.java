package com.example.bankcards.exception;

public class InsufficientFundsException extends ClientException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
