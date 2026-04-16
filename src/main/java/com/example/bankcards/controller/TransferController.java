package com.example.bankcards.controller;

import com.example.bankcards.api.user.TransferApi;
import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.user.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransferController implements TransferApi {

    private final TransferService transferService;

    @Override
    public TransactionElementResponse makeTransfer(TransactionRequest request, CustomUserDetails user) {
        return transferService.makeTransfer(request, user);
    }
}
