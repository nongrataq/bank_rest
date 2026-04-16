package com.example.bankcards.service.admin;

import com.example.bankcards.dto.request.CreateCardProductRequest;
import com.example.bankcards.dto.response.CardProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardProductManagementService {

    CardProductResponse createCard(CreateCardProductRequest request);

    Page<CardProductResponse> getAllProducts(boolean activeOnly, Pageable pageable);

    void deactivateCardProduct(UUID cardProductId);

    void activateCardProduct(UUID cardProductId);

    void deleteProduct(UUID cardProductId);
}
