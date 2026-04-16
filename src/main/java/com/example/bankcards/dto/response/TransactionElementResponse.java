package com.example.bankcards.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransactionElementResponse(
        UUID sourceCardId,
        UUID targetCardId,
        BigDecimal balance
) {
}
