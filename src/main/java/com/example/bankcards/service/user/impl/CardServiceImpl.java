package com.example.bankcards.service.user.impl;

import com.example.bankcards.dto.request.CardBlockRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.FullPanResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardProductEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardProductNotFound;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.CardProductRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.user.CardService;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardBlockRepository cardBlockRepository;
    private final UserRepository userRepository;
    private final CardProductRepository cardProductRepository;
    private final CardMapper cardMapper;
    private final Random random = new Random();
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponse> getMyCards(Pageable pageable, CustomUserDetails user) {
        Page<CardEntity> cards = cardRepository.getAllByUser_Id(user.getId(), pageable);
        return cards.map(cardMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public FullPanResponse revealFullPan(UUID cardId, CustomUserDetails user) {
        CardEntity card = cardRepository.findByIdAndUser_Id(cardId, user.getId())
                .orElseThrow(() -> new CardNotFoundException(cardId));

        String fullPan = encryptionUtil.decrypt(card.getEncryptedPan());

        log.warn("User {} revealed full PAN for card {}", user.getId(), cardId);

        return new FullPanResponse(fullPan);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getMyCard(UUID cardId, CustomUserDetails user) {
        CardEntity card = cardRepository.findByIdAndUser_Id(cardId, user.getId())
                .orElseThrow(() -> new CardNotFoundException(cardId));

        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    public CardResponse openMyCard(CardRequest cardRequest, CustomUserDetails user) {
        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        CardProductEntity cardProduct = cardProductRepository.findById(cardRequest.cardProductId())
                .orElseThrow(() -> new CardProductNotFound(cardRequest.cardProductId()));

        String generatedPan = generatePan();

        String encryptedPan = encryptionUtil.encrypt(generatedPan);

        String maskedPan = maskPan(generatedPan);

        CardEntity cardEntity = CardEntity.builder()
                .user(userEntity)
                .encryptedPan(encryptedPan)
                .cardProduct(cardProduct)
                .expiryDate(LocalDate.now().plusYears(5))
                .maskedPan(maskedPan)
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        CardEntity savedCard = cardRepository.save(cardEntity);

        log.info("New card created for user {}: {}", user.getId(), savedCard.getId());
        return cardMapper.toDto(savedCard);
    }

    @Override
    @Transactional
    public CardBlockResponse requestToBlockMyCard(UUID cardId,
                                                  CardBlockRequest request,
                                                  CustomUserDetails user) {

        CardEntity card = cardRepository.findByIdAndUser_Id(cardId, user.getId())
                .orElseThrow(() -> new CardNotFoundException(cardId));

        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        CardBlockRequestEntity blockRequest = CardBlockRequestEntity.builder()
                .card(card)
                .requestedBy(userEntity)
                .reason(request.reason())
                .build();

        CardBlockRequestEntity saved = cardBlockRepository.saveAndFlush(blockRequest);

        log.info("Block request created for card {} by user {}", cardId, user.getId());
        return cardMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteMyCard(UUID cardId, CustomUserDetails user) {
        log.info("User {} trying to delete card {}", user.getId(), cardId);

        CardEntity card = cardRepository.findByIdAndUser_Id(cardId, user.getId())
                .orElseThrow(() -> new CardNotFoundException(cardId));

        cardRepository.delete(card);

        log.info("Card {} successfully deleted by user {}", cardId, user.getId());
    }

    private String generatePan() {
        String pan;
        do {
            pan = String.valueOf(random.nextLong(1000_0000_0000_0000L, 9999_9999_9999_9999L + 1L));
        } while (cardRepository.existsByEncryptedPan(encryptionUtil.encrypt(pan)));

        return pan;
    }

    private String maskPan(String pan) {
        return "**** **** **** " + pan.substring(pan.length() - 4);
    }
}