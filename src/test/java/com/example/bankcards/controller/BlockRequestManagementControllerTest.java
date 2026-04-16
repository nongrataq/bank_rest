package com.example.bankcards.controller;

import com.example.bankcards.service.admin.BlockRequestManagementService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;  // <-- ДОБАВИТЬ
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlockRequestManagementController.class)
class BlockRequestManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockRequestManagementService blockRequestManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final UUID requestId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldApproveBlockRequest() throws Exception {
        doNothing().when(blockRequestManagementService).approveBlockRequest(any(), any(), any());

        String json = """
                {"reason": "Заявка отклонена"}
                """;

        mockMvc.perform(post("/api/v1/admin/block-requests/{requestId}/approve", requestId)
                        .with(csrf())
                        .content(json)// <-- ДОБАВИТЬ ЭТО
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectBlockRequest() throws Exception {
        String json = """
                {"reason": "Заявка отклонена"}
                """;

        doNothing().when(blockRequestManagementService).rejectBlockRequest(any(), any(), any());

        mockMvc.perform(post("/api/v1/admin/block-requests/{requestId}/reject", requestId)
                        .with(csrf())  // <-- ДОБАВИТЬ ЭТО
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllBlockRequests() throws Exception {
        mockMvc.perform(get("/api/v1/admin/block-requests")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenApproveWithoutReason() throws Exception {
        mockMvc.perform(post("/api/v1/admin/block-requests/{requestId}/approve", requestId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}