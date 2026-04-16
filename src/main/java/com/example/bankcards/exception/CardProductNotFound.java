package com.example.bankcards.exception;

import java.util.UUID;

public class CardProductNotFound extends NotFoundException {
    public CardProductNotFound(UUID id) {
        super("Card product with id %s not found".formatted(id));
    }
}
