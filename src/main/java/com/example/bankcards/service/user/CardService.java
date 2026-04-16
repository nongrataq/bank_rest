package com.example.bankcards.service.user;

import com.example.bankcards.dto.request.CardBlockRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.FullPanResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

public interface CardService {
    Page<CardResponse> getMyCards(Pageable pageable, CustomUserDetails user);

    CardBlockResponse requestToBlockMyCard(UUID cardId, CardBlockRequest request, CustomUserDetails user);

    CardResponse openMyCard(CardRequest cardRequest, CustomUserDetails user);

    CardResponse getMyCard(UUID cardId, CustomUserDetails user);

    void deleteMyCard(UUID cardId, CustomUserDetails user);

    FullPanResponse revealFullPan(UUID cardId, CustomUserDetails user);
}
