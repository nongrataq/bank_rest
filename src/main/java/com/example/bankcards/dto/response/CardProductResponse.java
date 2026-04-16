package com.example.bankcards.dto.response;

import java.util.UUID;

public record CardProductResponse(
        UUID id,
        String cardName,
        String description,
        Boolean isActive
) {}
