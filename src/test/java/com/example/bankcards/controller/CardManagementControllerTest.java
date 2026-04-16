package com.example.bankcards.controller;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.service.admin.CardManagementService;
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

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardManagementController.class)
class CardManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardManagementService cardManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final UUID cardId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldBlockCardByAdmin() throws Exception {
        doNothing().when(cardManagementService).blockUserCard(cardId);

        mockMvc.perform(post("/api/v1/admin/cards/{cardId}/block", cardId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllCardsWithFilters() throws Exception {
        mockMvc.perform(get("/api/v1/admin/cards")
                        .param("userId", UUID.randomUUID().toString())
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteCard() throws Exception {
        doNothing().when(cardManagementService).deleteCard(cardId);

        mockMvc.perform(delete("/api/v1/admin/cards/{cardId}", cardId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUnblockCardByAdmin() throws Exception {
        doNothing().when(cardManagementService).unblockUserCard(cardId);

        mockMvc.perform(post("/api/v1/admin/cards/{cardId}/unblock", cardId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}