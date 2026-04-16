package com.example.bankcards.service.user.impl;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.mapper.UserMapper;
import jakarta.validation.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {

        RoleEntity userRole = roleRepository.findByRole(RoleEntity.UserRole.USER)
                .orElseThrow(() -> new RoleNotFoundException(RoleEntity.UserRole.USER));

        UserEntity user = userMapper.toEntity(request);

        user.setHashPassword(passwordEncoder.encode(request.password()));

        user.getRoles().add(userRole);

        return userMapper.toDto(userRepository.save(user));
    }
}
