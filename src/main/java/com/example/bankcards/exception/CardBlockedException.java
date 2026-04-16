package com.example.bankcards.exception;

import java.util.UUID;

public class CardBlockedException extends AccessDeniedException {
    public CardBlockedException(UUID cardId) {
        super("Card with id %s has been blocked".formatted(cardId));
    }
}
