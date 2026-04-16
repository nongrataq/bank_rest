package com.example.bankcards.service.admin;

import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardManagementService {

    void blockUserCard(UUID cardId);

    void unblockUserCard(UUID cardId);

    Page<CardResponse> getAllCards(
            Pageable pageable,
            UUID userId,  // Фильтр по пользователю
            CardEntity.CardStatus status  // Фильтр по статусу
    );

    Page<CardBlockResponse> getAllCardBlockPendingRequests(Pageable pageable);

    void deleteCard(UUID cardId);
}
