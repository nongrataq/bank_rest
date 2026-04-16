package com.example.bankcards.repository;

import com.example.bankcards.entity.RefreshTokenEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByHashRefreshToken(String hashRefreshToken);

    List<RefreshTokenEntity> findByUserIdAndRevokedFalse(UUID userId);

    Optional<RefreshTokenEntity> findByJti(String jti);
}