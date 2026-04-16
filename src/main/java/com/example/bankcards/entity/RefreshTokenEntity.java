package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshTokenEntity extends BaseEntity {

    @Column(name = "hash_refresh_token", nullable = false)
    private String hashRefreshToken;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private boolean revoked = false;
}

