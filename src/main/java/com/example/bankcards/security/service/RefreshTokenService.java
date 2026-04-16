package com.example.bankcards.security.service;

import com.example.bankcards.dto.request.RefreshTokenRequest;
import com.example.bankcards.dto.response.TokenAuthenticationResponse;
import com.example.bankcards.entity.RefreshTokenEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.AuthenticationException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RefreshTokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.details.CustomUser;
import com.example.bankcards.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String generateRefreshToken(UUID userId) {
        log.debug("Generating refresh token for user: {}", userId);

        String jti = UUID.randomUUID().toString();
        String plainRefreshToken = "%s.%s.%d".formatted(
                jti,
                UUID.randomUUID().toString(),
                System.currentTimeMillis()
        );

        String hashedRefreshToken = passwordEncoder.encode(plainRefreshToken);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .jti(jti)
                .hashRefreshToken(hashedRefreshToken)
                .userId(userId)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(30)))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("New refresh token generated for user: {}", userId);
        return plainRefreshToken;
    }

    @Transactional(readOnly = true)
    public RefreshTokenEntity validateRefreshToken(String plainRefreshToken) {
        log.debug("Validating refresh token");

        String jti = extractJtiFromRefreshToken(plainRefreshToken);

        RefreshTokenEntity token = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

        if (!passwordEncoder.matches(plainRefreshToken, token.getHashRefreshToken())) {
            throw new AuthenticationException("Invalid refresh token");
        }

        if (token.isRevoked()) {
            throw new AuthenticationException("Refresh token has been revoked");
        }

        if (token.getExpiresAt().before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new AuthenticationException("Refresh token has expired");
        }

        log.debug("Refresh token is valid");

        return token;
    }

    private String extractJtiFromRefreshToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.contains(".")) {
            throw new AuthenticationException("Invalid refresh token format");
        }
        return refreshToken.split("\\.")[0];
    }

    @Transactional
    public TokenAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenEntity oldToken = validateRefreshToken(request.refreshToken());

        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken); //для наглядности, хотя dirty checking должен отработать

        UUID userId = oldToken.getUserId();

        CustomUserDetails userDetails = loadUserById(userId);
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = generateRefreshToken(userId);

        return TokenAuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private CustomUserDetails loadUserById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return new CustomUser(user);
    }
}