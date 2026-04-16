package com.example.bankcards.api.admin;

import com.example.bankcards.dto.request.ReasonRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@RequestMapping("/api/v1/admin/block-requests")
@SecurityRequirement(name = "Bearer Authentication")
public interface BlockRequestManagementApi {

    @GetMapping
    @Operation(summary = "Получить все заявки на блокировку", description = "Только заявки со статусом PENDING")
    Page<CardBlockResponse> getAllCardBlockPendingRequests(
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @PostMapping("/{requestId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Одобрить заявку на блокировку карты")
    @ApiResponse(responseCode = "204", description = "Заявка одобрена, карта заблокирована")
    void approveBlockRequest(@PathVariable UUID requestId,
                             @RequestBody @Valid ReasonRequest reason,
                             @AuthenticationPrincipal CustomUserDetails admin);

    @PostMapping("/{requestId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отклонить заявку на блокировку карты")
    void rejectBlockRequest(
            @PathVariable UUID requestId,
            @RequestBody @Valid ReasonRequest reason,
            @AuthenticationPrincipal CustomUserDetails admin
    );
}