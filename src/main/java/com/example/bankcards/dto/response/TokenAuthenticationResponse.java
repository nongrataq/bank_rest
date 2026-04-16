package com.example.bankcards.dto.response;

import lombok.Builder;

@Builder
public record TokenAuthenticationResponse(
        String accessToken,
        String refreshToken
) {
}
