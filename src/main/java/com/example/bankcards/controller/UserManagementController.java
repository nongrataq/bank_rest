package com.example.bankcards.controller;

import com.example.bankcards.api.admin.UserManagementApi;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.service.admin.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserManagementController implements UserManagementApi {

    private final UserManagementService userManagementService;

    @Override
    public void blockUserById(UUID userId) {
        userManagementService.blockUserById(userId);
    }

    @Override
    public void unblockUserById(UUID userId) {
        userManagementService.unblockUserById(userId);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userManagementService.getUserById(id);
    }

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userManagementService.getUsers(pageable);
    }

    @Override
    public UserResponse addRoleToUser(UUID id, RoleEntity.UserRole role) {
        return userManagementService.addRoleToUser(id, role);
    }

    @Override
    public UserResponse removeRoleFromUser(UUID id, RoleEntity.UserRole role) {
        return userManagementService.removeRoleFromUser(id, role);
    }
}
