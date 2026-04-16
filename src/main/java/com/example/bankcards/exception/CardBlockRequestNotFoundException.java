package com.example.bankcards.exception;

import java.util.UUID;

public class CardBlockRequestNotFoundException extends NotFoundException {
    public CardBlockRequestNotFoundException(UUID id) {
        super("Card block request with id: %s not found".formatted(id));
    }
}
