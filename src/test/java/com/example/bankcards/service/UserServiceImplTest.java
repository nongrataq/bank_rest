package com.example.bankcards.service;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.user.impl.UserServiceImpl;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private UserEntity userEntity;
    private UserResponse userResponse;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(
                "testuser",
                "test@example.com",
                "password123"
        );

        userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .roles(new HashSet<>())
                .build();

        userResponse = UserResponse.builder()
                .id(userEntity.getId())
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();

        userRole = RoleEntity.builder()
                .id(UUID.randomUUID())
                .role(RoleEntity.UserRole.USER)
                .build();
    }

    @Test
    void create_ShouldCreateNewUser() {
         
        when(roleRepository.findByRole(RoleEntity.UserRole.USER)).thenReturn(Optional.of(userRole));
        when(userMapper.toEntity(userRequest)).thenReturn(userEntity);
        when(passwordEncoder.encode(userRequest.password())).thenReturn("encoded_password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

         
        UserResponse result = userService.create(userRequest);

         
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.roles()).contains("USER");

        verify(userRepository).save(userEntity);
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void create_WhenRoleNotFound_ShouldThrowException() {
         
        when(roleRepository.findByRole(RoleEntity.UserRole.USER)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> userService.create(userRequest))
                .isInstanceOf(RoleNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void create_ShouldEncodePassword() {
         
        String rawPassword = "password123";
        String encodedPassword = "encoded_password_xyz";

        when(roleRepository.findByRole(RoleEntity.UserRole.USER)).thenReturn(Optional.of(userRole));
        when(userMapper.toEntity(userRequest)).thenReturn(userEntity);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

         
        userService.create(userRequest);

         
        verify(passwordEncoder).encode(rawPassword);
        assertThat(userEntity.getHashPassword()).isEqualTo(encodedPassword);
    }
}