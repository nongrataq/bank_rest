package com.example.bankcards.exception;

import java.util.UUID;

public class CardExpiredException extends ClientException {
    public CardExpiredException(UUID cardId) {
        super("Card with id %s has been expired".formatted(cardId));
    }
}
