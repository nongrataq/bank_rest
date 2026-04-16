package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardBlockRequestEntity;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CardBlockResponse(
        UUID id,
        CardBlockRequestEntity.RequestStatus status,
        UUID requestedBy,
        UUID cardId,
        String reason,
        Instant createdAt
) {
}
