package com.example.bankcards.api.user;

import com.example.bankcards.dto.request.RefreshTokenRequest;
import com.example.bankcards.dto.request.UserLoginRequest;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.TokenAuthenticationResponse;
import com.example.bankcards.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/auth")
public interface AuthApi {

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создаёт пользователя с ролью USER")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request);

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя", description = "Возвращает Access и Refresh токены")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная авторизация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    ResponseEntity<TokenAuthenticationResponse> login(@RequestBody @Valid UserLoginRequest request);

    @PostMapping("/refresh")
    @Operation(summary = "Обновление токенов", description = "Получение новой пары Access + Refresh токенов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Недействительный Refresh Token")
    })
    ResponseEntity<TokenAuthenticationResponse> refresh(@RequestBody @Valid RefreshTokenRequest request);
}