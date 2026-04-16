package com.example.bankcards.dto.request;

import com.example.bankcards.validation.NotEquals;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NotEquals(field1 = "sourceCardId", field2 = "targetCardId",
        message = "Source card and target card must be different")
public record TransactionRequest(

        @NotNull(message = "Source card ID is required")
        UUID sourceCardId,

        @NotNull(message = "Target card ID is required")
        UUID targetCardId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal balance
) {}