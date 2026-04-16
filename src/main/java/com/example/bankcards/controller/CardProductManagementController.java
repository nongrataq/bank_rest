package com.example.bankcards.controller;

import com.example.bankcards.api.admin.CardProductManagementApi;
import com.example.bankcards.dto.request.CreateCardProductRequest;
import com.example.bankcards.dto.response.CardProductResponse;
import com.example.bankcards.service.admin.CardProductManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CardProductManagementController implements CardProductManagementApi {

    private final CardProductManagementService cardProductManagementService;

    @Override
    public CardProductResponse createCard(CreateCardProductRequest request) {
        return cardProductManagementService.createCard(request);
    }

    @Override
    public Page<CardProductResponse> getAllProducts(boolean activeOnly, Pageable pageable) {
        return cardProductManagementService.getAllProducts(activeOnly, pageable);
    }

    @Override
    public void deactivateCardProduct(UUID cardProductId) {
        cardProductManagementService.deactivateCardProduct(cardProductId);
    }

    @Override
    public void activateCardProduct(UUID cardProductId) {
        cardProductManagementService.activateCardProduct(cardProductId);
    }

    @Override
    public void deleteProduct(UUID cardProductId) {
        cardProductManagementService.deleteProduct(cardProductId);
    }
}
