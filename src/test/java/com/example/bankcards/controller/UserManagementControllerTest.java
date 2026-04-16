package com.example.bankcards.controller;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.service.admin.UserManagementService;
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

@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final UUID userId = UUID.randomUUID();

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldBlockUser() throws Exception {
        doNothing().when(userManagementService).blockUserById(userId);

        mockMvc.perform(post("/api/v1/admin/users/block/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAddRoleToUser() throws Exception {
        UserResponse response = UserResponse.builder().id(userId).build();
        when(userManagementService.addRoleToUser(userId, RoleEntity.UserRole.ADMIN))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/roles", userId)
                        .with(csrf())
                        .param("role", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("role", "USER"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserById() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        when(userManagementService.getUserById(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUnblockUser() throws Exception {
        doNothing().when(userManagementService).unblockUserById(userId);

        mockMvc.perform(post("/api/v1/admin/users/unblock/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRemoveRoleFromUser() throws Exception {
        UserResponse response = UserResponse.builder().id(userId).build();
        when(userManagementService.removeRoleFromUser(userId, RoleEntity.UserRole.USER))
                .thenReturn(response);

        mockMvc.perform(delete("/api/v1/admin/users/{id}/roles", userId)
                        .with(csrf())
                        .param("role", "USER"))
                .andExpect(status().isOk());
    }
}