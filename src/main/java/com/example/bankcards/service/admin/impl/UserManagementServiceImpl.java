package com.example.bankcards.service.admin.impl;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.admin.UserManagementService;
import com.example.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void blockUserById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getLocked()) {
            log.info("User {} is already blocked", userId);
            return;
        }

        user.setLocked(true);
        userRepository.save(user);

        log.info("User {} has been blocked", userId);
    }

    @Override
    @Transactional
    public void unblockUserById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getLocked()) {
            log.info("User {} is not blocked", userId);
            return;
        }

        user.setLocked(false);
        userRepository.save(user);

        log.info("User {} has been unblocked", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserResponse addRoleToUser(UUID userId, RoleEntity.UserRole role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        RoleEntity roleEntity = roleRepository.findByRole(role)
                .orElseThrow(() -> new RoleNotFoundException(role));


        user.getRoles().add(roleEntity);

        return userMapper.toDto(userRepository.saveAndFlush(user));
    }

    @Override
    @Transactional
    public UserResponse removeRoleFromUser(UUID userId, RoleEntity.UserRole role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        RoleEntity roleEntity = roleRepository.findByRole(role)
                .orElseThrow(() -> new RoleNotFoundException(role));

        user.getRoles().remove(roleEntity);

        return userMapper.toDto(userRepository.saveAndFlush(user));
    }
}
