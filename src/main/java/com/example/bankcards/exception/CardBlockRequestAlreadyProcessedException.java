package com.example.bankcards.exception;

import java.util.UUID;

public class CardBlockRequestAlreadyProcessedException extends ClientException {
    public CardBlockRequestAlreadyProcessedException(UUID id) {
        super("Request: %s is already processed".formatted(id));
    }
}
