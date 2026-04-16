package com.example.bankcards.security.service;

import com.example.bankcards.dto.response.TokenValidationResponse;
import com.example.bankcards.security.details.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Duration jwtExpiration;

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", roles);
        claims.put("userId", userDetails.getId());
        claims.put("sub", userDetails.getUsername());
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtExpiration.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }


    public String getEmailFromToken(String token) {
        Claims allClaimsFromToken = getAllClaimsFromToken(token);
        String email = allClaimsFromToken.getSubject();
        log.debug("Email: {} parsed from token", email);
        return email;
    }

    public String getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", String.class);
    }

    public List<String> getRolesFromToken(String token) {
        List<String> roles = getAllClaimsFromToken(token).get("roles", List.class);
        log.info("Roles {} parsed from token", roles);
        return roles;
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            return TokenValidationResponse.builder()
                    .isValid(true)
                    .userId(UUID.fromString(getUserIdFromToken(token)))
                    .timestamp(LocalDateTime.now())
                    .roles(getRolesFromToken(token))
                    .email(getEmailFromToken(token))
                    .build();
        } catch (Exception e) {
            return TokenValidationResponse.builder()
                    .isValid(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

}
