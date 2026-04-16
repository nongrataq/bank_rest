package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCardProductRequest(
        @NotBlank(message = "Card name is required")
        @Pattern(
                regexp = "^[a-zA-Zа-яА-ЯёЁ\\s\\-]+$",
                message = "Card name must contain only letters, spaces, and hyphens"
        )
        @Size(min = 3, max = 100, message = "Card name must be between 3 and 100 characters")
        String cardName,

        @Size(max = 500, message = "Description is too long")
        String description,

        Boolean isActive
) {}