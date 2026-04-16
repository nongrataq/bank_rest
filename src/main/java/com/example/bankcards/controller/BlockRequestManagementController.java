package com.example.bankcards.controller;

import com.example.bankcards.api.admin.BlockRequestManagementApi;
import com.example.bankcards.dto.request.ReasonRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.admin.BlockRequestManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BlockRequestManagementController implements BlockRequestManagementApi {

    private final BlockRequestManagementService blockRequestManagementService;

    @Override
    public Page<CardBlockResponse> getAllCardBlockPendingRequests(Pageable pageable) {
        return blockRequestManagementService.getAllCardBlockPendingRequests(pageable);
    }

    @Override
    public void approveBlockRequest(UUID requestId, ReasonRequest request, CustomUserDetails admin) {
        blockRequestManagementService.approveBlockRequest(requestId, request, admin);
    }

    @Override
    public void rejectBlockRequest(UUID requestId, ReasonRequest reason, CustomUserDetails admin) {
        blockRequestManagementService.rejectBlockRequest(requestId, reason, admin);
    }
}
