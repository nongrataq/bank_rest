package com.example.bankcards.api.user;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/users/me/transfers/transactions")
@SecurityRequirement(name = "Bearer Authentication")
public interface TransferApi {

    @PostMapping
    @Operation(
            summary = "Перевод между своими картами",
            description = "Деньги переводятся только между картами одного пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств, карты не принадлежат пользователю или одинаковые карты"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Одна из карт не найдена")
    })
    TransactionElementResponse makeTransfer(
            @RequestBody @Valid TransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    );
}