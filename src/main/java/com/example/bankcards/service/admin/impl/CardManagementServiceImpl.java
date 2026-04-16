package com.example.bankcards.service.admin.impl;

import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardProductNotFound;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.admin.CardManagementService;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardManagementServiceImpl implements CardManagementService {

    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final CardBlockRepository cardBlockRepository;

    @Override
    @Transactional
    public void blockUserCard(UUID cardId) {
        CardEntity card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (card.getCardStatus() == CardEntity.CardStatus.BLOCKED) {
            log.info("Card {} is already blocked", cardId);
            return;
        }

        card.setCardStatus(CardEntity.CardStatus.BLOCKED);

        Optional<CardBlockRequestEntity> cardBlockRequestOpt = cardBlockRepository.findByCard_Id(cardId);
        cardBlockRequestOpt.ifPresent(
                entity -> entity.setStatus(CardBlockRequestEntity.RequestStatus.APPROVED)
        );

        cardRepository.save(card);

        log.info("Card {} blocked by admin", cardId);
    }

    @Override
    @Transactional
    public void unblockUserCard(UUID cardId) {
        CardEntity card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (card.getCardStatus() == CardEntity.CardStatus.ACTIVE) {
            log.info("Card {} is already active", cardId);
            return;
        }

        card.setCardStatus(CardEntity.CardStatus.ACTIVE);
        cardRepository.save(card);

        log.info("Card {} unblocked by admin", cardId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(Pageable pageable, UUID userId, CardEntity.CardStatus status) {
        if (userId != null && status != null) {
            return cardRepository.findAllByUser_IdAndCardStatus(userId, status, pageable).map(mapper::toDto);
        } else if (userId != null) {
            return cardRepository.findAllByUser_Id(userId, pageable).map(mapper::toDto);
        } else if (status != null) {
            return cardRepository.findByCardStatus(status, pageable).map(mapper::toDto);
        } else {
            return cardRepository.findAll(pageable).map(mapper::toDto);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardBlockResponse> getAllCardBlockPendingRequests(Pageable pageable) {
        return cardBlockRepository.findAllByStatus(CardBlockRequestEntity.RequestStatus.PENDING, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public void deleteCard(UUID cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new CardNotFoundException(cardId);
        }
        cardRepository.deleteById(cardId);
        log.info("Card {} deleted by admin", cardId);
    }
}
