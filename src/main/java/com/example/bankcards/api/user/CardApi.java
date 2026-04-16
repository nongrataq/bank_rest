package com.example.bankcards.api.user;

import com.example.bankcards.dto.request.CardBlockRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.FullPanResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/users/me/cards")
@SecurityRequirement(name = "Bearer Authentication")
public interface CardApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Открыть новую карту", description = "Пользователь может открыть карту только на своё имя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверный productId или ошибка валидации"),
            @ApiResponse(responseCode = "404", description = "Продукт карты не найден")
    })
    CardResponse openMyCard(@RequestBody @Valid CardRequest request,
                            @AuthenticationPrincipal CustomUserDetails user);

    @GetMapping
    @Operation(summary = "Получить список своих карт")
    Page<CardResponse> getMyCards(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user
    );

    @GetMapping("/{cardId}")
    @Operation(summary = "Получить одну свою карту")
    CardResponse getMyCard(@PathVariable UUID cardId,
                           @AuthenticationPrincipal CustomUserDetails user);

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить свою карту")
    void deleteMyCard(@PathVariable UUID cardId,
                      @AuthenticationPrincipal CustomUserDetails user);

    @PostMapping("/{cardId}/block")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Отправить запрос на блокировку карты")
    CardBlockResponse sendRequestToBlockMyCard(
            @PathVariable UUID cardId,
            @RequestBody(required = false) @Valid CardBlockRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    );

    @PostMapping("/{cardId}/reveal")
    @Operation(summary = "Показать полный номер карты (PAN)")
    FullPanResponse revealFullPan(@PathVariable UUID cardId,
                                  @AuthenticationPrincipal CustomUserDetails user);
}