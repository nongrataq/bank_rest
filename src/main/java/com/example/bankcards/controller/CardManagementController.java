package com.example.bankcards.controller;

import com.example.bankcards.api.admin.CardManagementApi;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.admin.CardManagementService;
import com.example.bankcards.service.user.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CardManagementController implements CardManagementApi {

    private final CardManagementService cardManagementService;

    @Override
    public void blockUserCard(UUID cardId) {
        cardManagementService.blockUserCard(cardId);
    }

    @Override
    public void unblockUserCard(UUID cardId) {
        cardManagementService.unblockUserCard(cardId);
    }

    @Override
    public void deleteCard(UUID cardId) {
        cardManagementService.deleteCard(cardId);
    }

    @Override
    public Page<CardResponse> getAllUserCards(Pageable pageable, UUID userId, CardEntity.CardStatus status) {
        return cardManagementService.getAllCards(pageable, userId, status);
    }
}
