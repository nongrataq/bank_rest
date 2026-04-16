package com.example.bankcards.service.admin;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserManagementService {
    void blockUserById(UUID userId);

    void unblockUserById(UUID userId);

    UserResponse getUserById(UUID id);

    Page<UserResponse> getUsers(Pageable pageable);

    UserResponse addRoleToUser(UUID userId, RoleEntity.UserRole role);

    UserResponse removeRoleFromUser(UUID userId,RoleEntity.UserRole role);
}
