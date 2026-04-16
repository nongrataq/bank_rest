package com.example.bankcards.api.admin;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/admin/cards")
@SecurityRequirement(name = "Bearer Authentication")
public interface CardManagementApi {

    @PostMapping("/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Экстренная блокировка карты", description = "Используется администратором для мгновенной блокировки")
    @ApiResponse(responseCode = "204", description = "Карта успешно заблокирована")
    void blockUserCard(@PathVariable UUID cardId);

    @PostMapping("/{cardId}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Разблокировка карты")
    void unblockUserCard(@PathVariable UUID cardId);

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление карты пользователя")
    void deleteCard(@PathVariable UUID cardId);

    @GetMapping
    @Operation(summary = "Получить все карты пользователей (с фильтрами)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    Page<CardResponse> getAllUserCards(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) CardEntity.CardStatus status
    );
}