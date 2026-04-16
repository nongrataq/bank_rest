package com.example.bankcards.service.admin.impl;

import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardBlockRepository;
import com.example.bankcards.repository.CardRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardManagementServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper mapper;

    @Mock
    private CardBlockRepository cardBlockRepository;

    @InjectMocks
    private CardManagementServiceImpl cardManagementService;

    private UUID cardId;
    private UUID userId;
    private CardEntity card;
    private CardResponse cardResponse;
    private CardBlockRequestEntity blockRequest;
    private CardBlockResponse blockResponse;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();

        card = CardEntity.builder()
                .id(cardId)
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .build();

        cardResponse = CardResponse.builder()
                .id(cardId)
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .build();

        blockRequest = CardBlockRequestEntity.builder()
                .id(UUID.randomUUID())
                .card(card)
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .build();

        blockResponse = CardBlockResponse.builder()
                .id(blockRequest.getId())
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .cardId(cardId)
                .build();
    }

    @Test
    void blockUserCard_ShouldBlockCard() {
         
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardBlockRepository.findByCard_Id(cardId)).thenReturn(Optional.empty());
        when(cardRepository.save(card)).thenReturn(card);

         
        cardManagementService.blockUserCard(cardId);

         
        assertThat(card.getCardStatus()).isEqualTo(CardEntity.CardStatus.BLOCKED);
        verify(cardRepository).save(card);
    }

    @Test
    void blockUserCard_WhenCardAlreadyBlocked_ShouldDoNothing() {
         
        card.setCardStatus(CardEntity.CardStatus.BLOCKED);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

         
        cardManagementService.blockUserCard(cardId);

         
        verify(cardRepository, never()).save(any());
    }

    @Test
    void blockUserCard_WhenCardNotFound_ShouldThrowException() {
         
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> cardManagementService.blockUserCard(cardId))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void blockUserCard_WhenBlockRequestExists_ShouldUpdateItsStatus() {
         
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardBlockRepository.findByCard_Id(cardId)).thenReturn(Optional.of(blockRequest));
        when(cardRepository.save(card)).thenReturn(card);

         
        cardManagementService.blockUserCard(cardId);

         
        assertThat(blockRequest.getStatus()).isEqualTo(CardBlockRequestEntity.RequestStatus.APPROVED);
        verify(cardRepository).save(card);
    }

    @Test
    void unblockUserCard_ShouldUnblockCard() {
         
        card.setCardStatus(CardEntity.CardStatus.BLOCKED);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

         
        cardManagementService.unblockUserCard(cardId);

         
        assertThat(card.getCardStatus()).isEqualTo(CardEntity.CardStatus.ACTIVE);
        verify(cardRepository).save(card);
    }

    @Test
    void unblockUserCard_WhenCardAlreadyActive_ShouldDoNothing() {
         
        card.setCardStatus(CardEntity.CardStatus.ACTIVE);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

         
        cardManagementService.unblockUserCard(cardId);

         
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getAllCards_WithoutFilters_ShouldReturnAllCards() {
         
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardEntity> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(mapper.toDto(card)).thenReturn(cardResponse);

         
        Page<CardResponse> result = cardManagementService.getAllCards(pageable, null, null);

         
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getAllCards_WithUserIdFilter_ShouldReturnUserCards() {
         
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardEntity> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findAllByUser_Id(userId, pageable)).thenReturn(cardPage);
        when(mapper.toDto(card)).thenReturn(cardResponse);

         
        Page<CardResponse> result = cardManagementService.getAllCards(pageable, userId, null);

         
        assertThat(result).isNotNull();
        verify(cardRepository).findAllByUser_Id(userId, pageable);
    }

    @Test
    void getAllCards_WithStatusFilter_ShouldReturnCardsByStatus() {
         
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardEntity> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findByCardStatus(CardEntity.CardStatus.ACTIVE, pageable)).thenReturn(cardPage);
        when(mapper.toDto(card)).thenReturn(cardResponse);

         
        Page<CardResponse> result = cardManagementService.getAllCards(pageable, null, CardEntity.CardStatus.ACTIVE);

         
        assertThat(result).isNotNull();
        verify(cardRepository).findByCardStatus(CardEntity.CardStatus.ACTIVE, pageable);
    }

    @Test
    void deleteCard_ShouldDeleteCard() {
         
        when(cardRepository.existsById(cardId)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(cardId);

         
        cardManagementService.deleteCard(cardId);

         
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void deleteCard_WhenCardNotFound_ShouldThrowException() {
         
        when(cardRepository.existsById(cardId)).thenReturn(false);

         
        assertThatThrownBy(() -> cardManagementService.deleteCard(cardId))
                .isInstanceOf(CardNotFoundException.class);

        verify(cardRepository, never()).deleteById(any());
    }
}