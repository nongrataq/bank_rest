package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardRequest(
        @NotNull(message = "Card product ID is required")
        UUID cardProductId
) {}