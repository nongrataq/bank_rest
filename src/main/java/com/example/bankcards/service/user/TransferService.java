package com.example.bankcards.service.user;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import com.example.bankcards.security.details.CustomUserDetails;

public interface TransferService {
    TransactionElementResponse makeTransfer(TransactionRequest request, CustomUserDetails user);
}
