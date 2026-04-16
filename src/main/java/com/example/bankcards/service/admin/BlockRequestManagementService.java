package com.example.bankcards.service.admin;


import com.example.bankcards.dto.request.ReasonRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BlockRequestManagementService {

    Page<CardBlockResponse> getAllCardBlockPendingRequests(Pageable pageable);

    void approveBlockRequest(UUID requestId, ReasonRequest request, CustomUserDetails admin);

    void rejectBlockRequest(UUID requestId, ReasonRequest request, CustomUserDetails admin);
}