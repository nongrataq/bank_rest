package com.example.bankcards.security.filter;


import com.example.bankcards.dto.response.TokenValidationResponse;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.security.details.CustomUser;
import com.example.bankcards.security.details.CustomUserDetails;
import com.example.bankcards.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        log.info("Authorization header: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String bearerToken = authorizationHeader.substring(7);

        TokenValidationResponse validated = jwtService.validateToken(bearerToken);
        if (validated.isValid()) {
            log.debug("Token valid for user: {}", validated.email());
            List<SimpleGrantedAuthority> roles = validated.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            CustomUserDetails userDetails = new CustomUser(
                    UserEntity.builder()
                            .id(validated.userId())
                            .email(validated.email())
                            .build()
            );

            var token = new UsernamePasswordAuthenticationToken(userDetails, null, roles);

            SecurityContextHolder.getContext().setAuthentication(token);
            filterChain.doFilter(request, response);
        } else {
            log.warn("Invalid token: {}", validated.error());
            filterChain.doFilter(request, response);
        }
    }
}
