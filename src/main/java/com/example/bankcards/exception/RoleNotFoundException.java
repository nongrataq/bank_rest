package com.example.bankcards.exception;


import com.example.bankcards.entity.RoleEntity;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(RoleEntity.UserRole role) {
        super("Role %s not found".formatted(role));
    }
}
