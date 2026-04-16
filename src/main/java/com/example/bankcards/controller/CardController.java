package com.example.bankcards.controller;

import com.example.bankcards.api.user.CardApi;
import com.example.bankcards.dto.request.CardBlockRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.FullPanResponse;
import com.example.bankcards.security.details.CustomUserDetails;
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
public class CardController implements CardApi {

    private final CardService userCardService;

    @Override
    public CardResponse openMyCard(CardRequest request, CustomUserDetails user) {
        return userCardService.openMyCard(request, user);
    }

    @Override
    public Page<CardResponse> getMyCards(Pageable pageable, CustomUserDetails user) {
        return userCardService.getMyCards(pageable, user);
    }

    @Override
    public CardResponse getMyCard(UUID cardId, CustomUserDetails user) {
        return userCardService.getMyCard(cardId, user);
    }

    @Override
    public void deleteMyCard(UUID cardId, CustomUserDetails user) {
        userCardService.deleteMyCard(cardId, user);
    }

    @Override
    public CardBlockResponse sendRequestToBlockMyCard(UUID cardId, CardBlockRequest request, CustomUserDetails user) {
        return userCardService.requestToBlockMyCard(cardId, request, user);
    }

    @Override
    public FullPanResponse revealFullPan(UUID cardId, CustomUserDetails user) {
        return userCardService.revealFullPan(cardId, user);
    }
}
