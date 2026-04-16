package com.example.bankcards.service;

import com.example.bankcards.dto.response.TokenValidationResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.security.details.CustomUser;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final Duration jwtExpiration = Duration.ofHours(1);

    private UUID userId;
    private String email;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@example.com";
         
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email(email)
                .build();
         
        RoleEntity role = RoleEntity.builder()
                .id(UUID.randomUUID())
                .role(RoleEntity.UserRole.USER)
                .build();
        userEntity.setRoles(Set.of(role));
         
        userDetails = new CustomUser(userEntity);

        String secretKey = "mySuperSecretKeyForJWTThatIsAtLeast32CharactersLong";
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
    }

    @Test
    void generateToken_ShouldCreateValidJwtToken() {
         
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void getEmailFromToken_ShouldExtractEmail() {
         
        String token = jwtService.generateToken(userDetails);

        String extractedEmail = jwtService.getEmailFromToken(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void getUserIdFromToken_ShouldExtractUserId() {
         
        String token = jwtService.generateToken(userDetails);

        UUID extractedUserId = UUID.fromString(jwtService.getUserIdFromToken(token));

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void getRolesFromToken_ShouldExtractRoles() {
         
        String token = jwtService.generateToken(userDetails);

        List<String> roles = jwtService.getRolesFromToken(token);

        assertThat(roles).contains("ROLE_USER");
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnValidResponse() {
         
        String token = jwtService.generateToken(userDetails);
         
        TokenValidationResponse response = jwtService.validateToken(token);

        assertThat(response.isValid()).isTrue();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.roles()).contains("ROLE_USER");
        assertThat(response.error()).isNull();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnInvalidResponse() {
         
        String invalidToken = "invalid.token.here";

        TokenValidationResponse response = jwtService.validateToken(invalidToken);

        assertThat(response.isValid()).isFalse();
        assertThat(response.error()).isNotBlank();
    }
}