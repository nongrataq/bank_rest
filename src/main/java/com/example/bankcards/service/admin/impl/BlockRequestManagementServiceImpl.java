package com.example.bankcards.service.admin.impl;

import com.example.bankcards.dto.request.ReasonRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.CardBlockRequestNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.admin.BlockRequestManagementService;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockRequestManagementServiceImpl implements BlockRequestManagementService {

    private final CardBlockRepository cardBlockRepository;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CardBlockResponse> getAllCardBlockPendingRequests(Pageable pageable) {
        return cardBlockRepository.findAllByStatus(CardBlockRequestEntity.RequestStatus.PENDING, pageable)
                .map(cardMapper::toDto);
    }

    @Override
    @Transactional
    public void approveBlockRequest(UUID requestId, ReasonRequest reason, CustomUserDetails admin) {
        CardBlockRequestEntity request = cardBlockRepository.findById(requestId)
                .orElseThrow(() -> new CardBlockRequestNotFoundException(requestId));

        if (request.getStatus() != CardBlockRequestEntity.RequestStatus.PENDING) {
            throw new CardBlockRequestNotFoundException(requestId);
        }

        CardEntity card = request.getCard();
        card.setCardStatus(CardEntity.CardStatus.BLOCKED);
        cardRepository.save(card);

        request.setStatus(CardBlockRequestEntity.RequestStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(userRepository.findById(admin.getId()).orElseThrow(
                () -> new UserNotFoundException(admin.getId())
        ));
        request.setReason(reason.reason());

        cardBlockRepository.save(request);

        log.info("Block request {} approved. Card {} blocked.", requestId, card.getId());
    }

    @Override
    @Transactional
    public void rejectBlockRequest(UUID requestId, ReasonRequest reason, CustomUserDetails admin) {
        CardBlockRequestEntity request = cardBlockRepository.findById(requestId)
                .orElseThrow(() -> new CardBlockRequestNotFoundException(requestId));

        if (request.getStatus() != CardBlockRequestEntity.RequestStatus.PENDING) {
            throw new CardBlockRequestNotFoundException(requestId);
        }

        request.setStatus(CardBlockRequestEntity.RequestStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(userRepository.findById(admin.getId()).orElseThrow(
                () -> new UserNotFoundException(admin.getId())
        ));
        request.setReason(reason.reason());

        cardBlockRepository.save(request);

        log.info("Block request {} rejected. Reason: {}", requestId, reason.reason());
    }
}