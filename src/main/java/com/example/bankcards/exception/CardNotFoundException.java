package com.example.bankcards.exception;

import java.util.UUID;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(UUID cardId) {
        super("Card with id: %s not found".formatted(cardId));
    }
}
