package com.example.bankcards.exception;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(UUID userId) {
        super("User with id: %s not found".formatted(userId));
    }
}
