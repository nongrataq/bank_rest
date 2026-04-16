package com.example.bankcards.api.admin;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/admin/users")
@SecurityRequirement(name = "Bearer Authentication")
public interface UserManagementApi {

    @PostMapping("/block/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Заблокировать пользователя")
    void blockUserById(@PathVariable UUID userId);

    @PostMapping("/unblock/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Разблокировать пользователя")
    void unblockUserById(@PathVariable UUID userId);

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    UserResponse getUserById(@PathVariable UUID id);

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    Page<UserResponse> getUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @PatchMapping("/{id}/roles")
    @Operation(summary = "Добавить роль пользователю")
    UserResponse addRoleToUser(
            @PathVariable UUID id,
            @RequestParam(name = "role") RoleEntity.UserRole role
    );

    @DeleteMapping("/{id}/roles")
    @Operation(summary = "Удалить роль у пользователя")
    UserResponse removeRoleFromUser(
            @PathVariable UUID id,
            @RequestParam(name = "role") RoleEntity.UserRole role
    );
}