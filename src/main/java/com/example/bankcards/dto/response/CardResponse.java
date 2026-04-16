package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CardResponse(
        UUID id,
        String maskedPan,
        String lastFour,
        String cardholderName,
        String expiryDate,
        CardEntity.CardStatus cardStatus,
        BigDecimal balance,
        CardProductResponse cardProduct,
        UUID userId
) {}