package com.example.bankcards.service.user.impl;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.util.mapper.TransferMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private UUID userId;
    private UUID sourceCardId;
    private UUID targetCardId;
    private CustomUserDetails userDetails;
    private CardEntity sourceCard;
    private CardEntity targetCard;
    private TransactionRequest transactionRequest;
    private TransactionElementResponse transactionResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sourceCardId = UUID.randomUUID();
        targetCardId = UUID.randomUUID();

        userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);

        sourceCard = CardEntity.builder()
                .id(sourceCardId)
                .balance(new BigDecimal("1000"))
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .build();

        targetCard = CardEntity.builder()
                .id(targetCardId)
                .balance(new BigDecimal("500"))
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .build();

        transactionRequest = TransactionRequest.builder()
                .sourceCardId(sourceCardId)
                .targetCardId(targetCardId)
                .balance(new BigDecimal("200"))
                .build();

        transactionResponse = TransactionElementResponse.builder()
                .sourceCardId(sourceCardId)
                .targetCardId(targetCardId)
                .balance(new BigDecimal("200"))
                .build();
    }

    @Test
    void makeTransfer_ShouldSuccessfullyTransferMoney() {
         
        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId))
                .thenReturn(Optional.of(targetCard));
        when(transferMapper.toResponse(transactionRequest)).thenReturn(transactionResponse);

         
        TransactionElementResponse result = transferService.makeTransfer(transactionRequest, userDetails);

         
        assertThat(result).isNotNull();
        assertThat(result.balance()).isEqualTo(new BigDecimal("200"));
        assertThat(sourceCard.getBalance()).isEqualTo(new BigDecimal("800"));
        assertThat(targetCard.getBalance()).isEqualTo(new BigDecimal("700"));

        verify(cardRepository).save(sourceCard);
        verify(cardRepository).save(targetCard);
    }

    @Test
    void makeTransfer_WhenSourceCardNotFound_ShouldThrowException() {
         
        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> transferService.makeTransfer(transactionRequest, userDetails))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(sourceCardId.toString());

        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_WhenTargetCardNotFound_ShouldThrowException() {
         
        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId))
                .thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> transferService.makeTransfer(transactionRequest, userDetails))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(targetCardId.toString());

        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_WhenSourceCardNotActive_ShouldThrowException() {
         
        sourceCard.setCardStatus(CardEntity.CardStatus.BLOCKED);
        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId))
                .thenReturn(Optional.of(targetCard));

         
        assertThatThrownBy(() -> transferService.makeTransfer(transactionRequest, userDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not active");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_WhenTargetCardNotActive_ShouldThrowException() {
         
        targetCard.setCardStatus(CardEntity.CardStatus.BLOCKED);
        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId))
                .thenReturn(Optional.of(targetCard));

         
        assertThatThrownBy(() -> transferService.makeTransfer(transactionRequest, userDetails))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("not active");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_WhenInsufficientFunds_ShouldThrowException() {
         
        transactionRequest = TransactionRequest.builder()
                .sourceCardId(sourceCardId)
                .targetCardId(targetCardId)
                .balance(new BigDecimal("2000"))
                .build();

        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId))
                .thenReturn(Optional.of(targetCard));

         
        assertThatThrownBy(() -> transferService.makeTransfer(transactionRequest, userDetails))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Недостаточно средств");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void makeTransfer_WhenExactAmountTransfer_ShouldWork() {
         
        transactionRequest = TransactionRequest.builder()
                .sourceCardId(sourceCardId)
                .targetCardId(targetCardId)
                .balance(new BigDecimal("1000"))
                .build();

        when(cardRepository.findByIdAndUser_IdWithLock(sourceCardId, userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(targetCardId))
                .thenReturn(Optional.of(targetCard));
        when(transferMapper.toResponse(transactionRequest)).thenReturn(transactionResponse);

         
        transferService.makeTransfer(transactionRequest, userDetails);

         
        assertThat(sourceCard.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(targetCard.getBalance()).isEqualTo(new BigDecimal("1500"));
    }
}