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
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.mapper.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardBlockRepository cardBlockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardProductRepository cardProductRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private CardServiceImpl cardService;

    private UUID userId;
    private UUID cardId;
    private UUID cardProductId;
    private CustomUserDetails userDetails;
    private UserEntity userEntity;
    private CardEntity cardEntity;
    private CardProductEntity cardProductEntity;
    private CardResponse cardResponse;
    private CardBlockRequest blockRequest;
    private CardBlockRequestEntity blockRequestEntity;
    private CardBlockResponse blockResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cardId = UUID.randomUUID();
        cardProductId = UUID.randomUUID();

        userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .build();

        cardProductEntity = CardProductEntity.builder()
                .id(cardProductId)
                .cardName("Classic Card")
                .isActive(true)
                .build();

        cardEntity = CardEntity.builder()
                .id(cardId)
                .user(userEntity)
                .cardProduct(cardProductEntity)
                .maskedPan("**** **** **** 1234")
                .balance(BigDecimal.ZERO)
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .expiryDate(LocalDate.now().plusYears(5))
                .build();

        cardResponse = CardResponse.builder()
                .id(cardId)
                .maskedPan("**** **** **** 1234")
                .lastFour("1234")
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        blockRequest = CardBlockRequest.builder()
                .reason("Card lost")
                .build();

        blockRequestEntity = CardBlockRequestEntity.builder()
                .id(UUID.randomUUID())
                .card(cardEntity)
                .requestedBy(userEntity)
                .reason("Card lost")
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .build();

        blockResponse = CardBlockResponse.builder()
                .id(blockRequestEntity.getId())
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .cardId(cardId)
                .requestedBy(userId)
                .reason("Card lost")
                .build();
    }

    @Test
    void getMyCards_ShouldReturnPageOfCards() {
         
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardEntity> cardPage = new PageImpl<>(List.of(cardEntity));
        when(cardRepository.getAllByUser_Id(userId, pageable)).thenReturn(cardPage);
        when(cardMapper.toDto(cardEntity)).thenReturn(cardResponse);

         
        Page<CardResponse> result = cardService.getMyCards(pageable, userDetails);

         
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(cardId);
        verify(cardRepository).getAllByUser_Id(userId, pageable);
    }

    @Test
    void getMyCard_WhenCardExists_ShouldReturnCard() {
         
        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toDto(cardEntity)).thenReturn(cardResponse);

         
        CardResponse result = cardService.getMyCard(cardId, userDetails);

         
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(cardId);
        verify(cardRepository).findByIdAndUser_Id(cardId, userId);
    }

    @Test
    void getMyCard_WhenCardNotFound_ShouldThrowException() {
         
        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> cardService.getMyCard(cardId, userDetails))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(cardId.toString());
    }

    @Test
    void revealFullPan_ShouldReturnDecryptedPan() {
         
        String encryptedPan = "encrypted_pan_123";
        String decryptedPan = "1234567890123456";
        cardEntity.setEncryptedPan(encryptedPan);

        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.of(cardEntity));
        when(encryptionUtil.decrypt(encryptedPan)).thenReturn(decryptedPan);

        FullPanResponse result = cardService.revealFullPan(cardId, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.pan()).isEqualTo(decryptedPan);
        verify(encryptionUtil).decrypt(encryptedPan);
    }

    @Test
    void openMyCard_ShouldCreateNewCard() {
        CardRequest cardRequest = new CardRequest(cardProductId);
        String generatedPan = "1234567890123456";
        String encryptedPan = "encrypted_pan";
        String maskedPan = "**** **** **** 3456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(cardProductRepository.findById(cardProductId)).thenReturn(Optional.of(cardProductEntity));
        when(cardRepository.existsByEncryptedPan(anyString())).thenReturn(false);
        when(encryptionUtil.encrypt(anyString())).thenReturn(encryptedPan);
        when(cardRepository.save(any(CardEntity.class))).thenReturn(cardEntity);
        when(cardMapper.toDto(cardEntity)).thenReturn(cardResponse);

        CardResponse result = cardService.openMyCard(cardRequest, userDetails);

        assertThat(result).isNotNull();
        verify(cardRepository).save(any(CardEntity.class));
    }

    @Test
    void openMyCard_WhenUserNotFound_ShouldThrowException() {
        CardRequest cardRequest = new CardRequest(cardProductId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.openMyCard(cardRequest, userDetails))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void openMyCard_WhenCardProductNotFound_ShouldThrowException() {
        CardRequest cardRequest = new CardRequest(cardProductId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(cardProductRepository.findById(cardProductId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.openMyCard(cardRequest, userDetails))
                .isInstanceOf(CardProductNotFound.class)
                .hasMessageContaining(cardProductId.toString());
    }

    @Test
    void requestToBlockMyCard_ShouldCreateBlockRequest() {
        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.of(cardEntity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(cardBlockRepository.saveAndFlush(any(CardBlockRequestEntity.class))).thenReturn(blockRequestEntity);
        when(cardMapper.toDto(blockRequestEntity)).thenReturn(blockResponse);

        CardBlockResponse result = cardService.requestToBlockMyCard(cardId, blockRequest, userDetails);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(CardBlockRequestEntity.RequestStatus.PENDING);
        verify(cardBlockRepository).saveAndFlush(any(CardBlockRequestEntity.class));
    }

    @Test
    void requestToBlockMyCard_WhenCardNotFound_ShouldThrowException() {
        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.requestToBlockMyCard(cardId, blockRequest, userDetails))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void deleteMyCard_ShouldDeleteCard() {
        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.of(cardEntity));
        doNothing().when(cardRepository).delete(cardEntity);

        cardService.deleteMyCard(cardId, userDetails);

         
        verify(cardRepository).delete(cardEntity);
    }

    @Test
    void deleteMyCard_WhenCardNotFound_ShouldThrowException() {
         
        when(cardRepository.findByIdAndUser_Id(cardId, userId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> cardService.deleteMyCard(cardId, userDetails))
                .isInstanceOf(CardNotFoundException.class);
    }
}