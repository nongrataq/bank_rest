package com.example.bankcards.api.admin;

import com.example.bankcards.dto.request.CreateCardProductRequest;
import com.example.bankcards.dto.response.CardProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/admin/products")
@SecurityRequirement(name = "Bearer Authentication")
public interface CardProductManagementApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый продукт карты (тип карты)")
    @ApiResponse(responseCode = "201", description = "Продукт успешно создан")
    CardProductResponse createCard(@RequestBody @Valid CreateCardProductRequest request);

    @GetMapping
    @Operation(summary = "Получить список продуктов карт")
    Page<CardProductResponse> getAllProducts(
            @RequestParam(required = false) boolean activeOnly,
            Pageable pageable
    );

    @PostMapping("/{cardProductId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Деактивировать продукт карты")
    void deactivateCardProduct(@PathVariable UUID cardProductId);

    @PostMapping("/{cardProductId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Активировать продукт карты")
    void activateCardProduct(@PathVariable UUID cardProductId);

    @DeleteMapping("/{cardProductId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить продукт карты")
    void deleteProduct(@PathVariable UUID cardProductId);
}