package com.example.bankcards.dto.response;

import com.example.bankcards.entity.RoleEntity;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse (
        UUID id,
        String username,
        String email,
        Set<String> roles
) {
}
