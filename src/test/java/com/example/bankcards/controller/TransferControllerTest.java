package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.user.TransferService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser(roles = "USER")
    void shouldMakeTransfer() throws Exception {
        TransactionElementResponse response = TransactionElementResponse.builder()
                .sourceCardId(UUID.randomUUID())
                .targetCardId(UUID.randomUUID())
                .balance(new BigDecimal("1500"))
                .build();

        when(transferService.makeTransfer(any(), any())).thenReturn(response);

        String json = """
                {
                    "sourceCardId": "11111111-1111-1111-1111-111111111111",
                    "targetCardId": "22222222-2222-2222-2222-222222222222",
                    "balance": 1500
                }
                """;

        mockMvc.perform(post("/api/v1/users/me/transfers/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }
}