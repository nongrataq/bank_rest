package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReasonRequest(
        @NotBlank(message = "Reason is required")
        @Size(max = 300, message = "Reason is too long")
        String reason
) {}