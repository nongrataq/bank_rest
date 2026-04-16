package com.example.bankcards.service.admin.impl;

import com.example.bankcards.dto.request.CreateCardProductRequest;
import com.example.bankcards.dto.response.CardProductResponse;
import com.example.bankcards.entity.CardProductEntity;
import com.example.bankcards.exception.CardProductNotFound;
import com.example.bankcards.repository.CardProductRepository;
import com.example.bankcards.service.admin.CardProductManagementService;
import com.example.bankcards.util.mapper.CardProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardProductManagementServiceImpl implements CardProductManagementService {

    private final CardProductRepository repository;
    private final CardProductMapper mapper;

    @Override
    @Transactional
    public CardProductResponse createCard(CreateCardProductRequest request) {
        CardProductEntity entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardProductResponse> getAllProducts(boolean activeOnly, Pageable pageable) {
        return repository.findAllByIsActive(activeOnly, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public void deactivateCardProduct(UUID cardProductId) {
        CardProductEntity cardProduct = repository.findById(cardProductId)
                .orElseThrow(() -> new CardProductNotFound(cardProductId));

        if (!cardProduct.getIsActive()) {
            return;
        }

        cardProduct.setIsActive(false);
    }

    @Override
    @Transactional
    public void activateCardProduct(UUID cardProductId) {
        CardProductEntity cardProduct = repository.findById(cardProductId)
                .orElseThrow(() -> new CardProductNotFound(cardProductId));

        if (cardProduct.getIsActive()) {
            return;
        }

        cardProduct.setIsActive(true);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID cardProductId) {
        if (!repository.existsById(cardProductId)) {
            throw new CardProductNotFound(cardProductId);
        }
        repository.deleteById(cardProductId);
    }
}
