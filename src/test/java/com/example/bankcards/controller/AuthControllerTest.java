package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UserLoginRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.TokenAuthenticationResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.security.details.CustomUser;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.security.service.JwtService;
import com.example.bankcards.security.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldRegisterUser() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();

        when(userService.create(any(UserRequest.class))).thenReturn(response);

        String json = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
    @Test
    void shouldReturnBadRequestWhenRegisterWithInvalidData() throws Exception {
        String json = """
                {
                    "username": "",
                    "email": "invalid-email",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginUser() throws Exception {
        String token = "jwt-token-123";
        String refreshToken = "refresh-token-123";

        TokenAuthenticationResponse response = TokenAuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@mail.ru");
        when(userDetails.getId()).thenReturn(UUID.randomUUID());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn(token);
        when(refreshTokenService.generateRefreshToken(any(UUID.class))).thenReturn(refreshToken);

        String json = """
            {
                "email": "testuser@mail.ru",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        String json = """
                {
                    "email": "wronguser@mail.ru",
                    "password": "wrongpass"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}