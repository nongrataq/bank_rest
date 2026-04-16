package com.example.bankcards.controller;

import com.example.bankcards.api.user.AuthApi;
import com.example.bankcards.dto.request.RefreshTokenRequest;
import com.example.bankcards.dto.request.UserLoginRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.TokenAuthenticationResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.security.service.JwtService;
import com.example.bankcards.security.service.RefreshTokenService;
import com.example.bankcards.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController implements AuthApi {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public ResponseEntity<UserResponse> register(UserRequest request) {
        UserResponse user = userService.create(request);
        log.info("New user registered: {}", user.username());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @Override
    public ResponseEntity<TokenAuthenticationResponse> login(UserLoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(principal);
            String refreshToken = refreshTokenService.generateRefreshToken(principal.getId());

            log.info("User logged in successfully: {}", principal.getUsername());

            return ResponseEntity.ok(
                    TokenAuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build()
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for {}", request.email());
            throw new com.example.bankcards.exception.AuthenticationException("Invalid email or password");
        }
    }

    @Override
    public ResponseEntity<TokenAuthenticationResponse> refresh(RefreshTokenRequest request) {
        log.info("User try to refresh");
        return ResponseEntity.ok(refreshTokenService.refreshToken(request));
    }
}


