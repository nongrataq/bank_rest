package com.example.bankcards.service.user.impl;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardProductNotFound;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.user.TransferService;
import com.example.bankcards.util.mapper.TransferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final TransferMapper transferMapper;

    @Override
    @Transactional
    public TransactionElementResponse makeTransfer(TransactionRequest request, CustomUserDetails user) {
        CardEntity sourceCard = cardRepository.findByIdAndUser_IdWithLock(request.sourceCardId(), user.getId())
                .orElseThrow(() -> new CardNotFoundException(request.sourceCardId()));

        CardEntity targetCard = cardRepository.findById(request.targetCardId())
                .orElseThrow(() -> new CardNotFoundException(request.targetCardId()));

        if (!sourceCard.isActive() || !targetCard.isActive()) {
            throw new AccessDeniedException("One of the cards is not active");
        }

        BigDecimal transferAmount = request.balance();
        BigDecimal currentBalance = sourceCard.getBalance();

        if (currentBalance.compareTo(transferAmount) < 0) {
            throw new InsufficientFundsException(
                    "Недостаточно средств. Доступно: %s, требуется: %s"
                            .formatted(currentBalance, transferAmount)
            );
        }

        sourceCard.setBalance(currentBalance.subtract(transferAmount));
        targetCard.setBalance(targetCard.getBalance().add(transferAmount));

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);

        log.info("Transfer {} -> {} amount: {}", sourceCard.getId(), targetCard.getId(), transferAmount);

        return transferMapper.toResponse(request);
    }
}
