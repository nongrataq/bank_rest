package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardBlockRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.user.CardService;
import com.example.bankcards.security.service.JwtService;
import com.example.bankcards.security.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final UUID cardId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "USER")
    void shouldOpenNewCard() throws Exception {
        CardResponse response = CardResponse.builder()
                .id(cardId)
                .maskedPan("**** **** **** 1234")
                .lastFour("1234")
                .cardholderName("Test User")
                .expiryDate("04/31")
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        when(cardService.openMyCard(any(), any())).thenReturn(response);

        String json = """
                {"cardProductId": "550e8400-e29b-41d4-a716-446655440000"}
                """;

        mockMvc.perform(post("/api/v1/users/me/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maskedPan").value("**** **** **** 1234"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldSendBlockRequest() throws Exception {
        CardBlockResponse response = CardBlockResponse.builder()
                .id(UUID.randomUUID())
                .status(CardBlockRequestEntity.RequestStatus.PENDING)
                .build();

        when(cardService.requestToBlockMyCard(any(), any(), any())).thenReturn(response);

        String json = """
                {"reason": "Потерял карту"}
                """;

        mockMvc.perform(post("/api/v1/users/me/cards/{cardId}/block", cardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetMyCards() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetCardById() throws Exception {
        CardResponse response = CardResponse.builder()
                .id(cardId)
                .maskedPan("**** **** **** 1234")
                .cardStatus(CardEntity.CardStatus.ACTIVE)
                .build();

        when(cardService.getMyCard(eq(cardId), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me/cards/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnNotFoundWhenCardNotExists() throws Exception {
        when(cardService.getMyCard(eq(cardId), any()))
                .thenThrow(new CardNotFoundException(cardId));

        mockMvc.perform(get("/api/v1/users/me/cards/{cardId}", cardId))
                .andExpect(status().isNotFound());
    }
}