package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CardBlockRequest(
        @NotBlank(message = "Reason for blocking is required")
        @Size(max = 300, message = "Reason must not exceed 300 characters")
        String reason
) {}