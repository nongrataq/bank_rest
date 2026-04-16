package com.example.bankcards.service;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.admin.impl.UserManagementServiceImpl;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private UUID userId;
    private UserEntity user;
    private UserResponse userResponse;
    private RoleEntity adminRole;
    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .locked(false)
                .roles(new HashSet<>())
                .build();

        userResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .build();

        adminRole = RoleEntity.builder()
                .id(UUID.randomUUID())
                .role(RoleEntity.UserRole.ADMIN)
                .build();

        userRole = RoleEntity.builder()
                .id(UUID.randomUUID())
                .role(RoleEntity.UserRole.USER)
                .build();
    }

    @Test
    void blockUserById_ShouldBlockUser() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

         
        userManagementService.blockUserById(userId);

         
        assertThat(user.getLocked()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void blockUserById_WhenUserAlreadyBlocked_ShouldDoNothing() {
         
        user.setLocked(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

         
        userManagementService.blockUserById(userId);

         
        verify(userRepository, never()).save(any());
    }

    @Test
    void blockUserById_WhenUserNotFound_ShouldThrowException() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> userManagementService.blockUserById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void unblockUserById_ShouldUnblockUser() {
         
        user.setLocked(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

         
        userManagementService.unblockUserById(userId);

         
        assertThat(user.getLocked()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void unblockUserById_WhenUserNotBlocked_ShouldDoNothing() {
         
        user.setLocked(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

         
        userManagementService.unblockUserById(userId);

         
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUser() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

         
        UserResponse result = userManagementService.getUserById(userId);

         
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowException() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> userManagementService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUsers_ShouldReturnPageOfUsers() {
         
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userResponse);

         
        Page<UserResponse> result = userManagementService.getUsers(pageable);

         
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void addRoleToUser_ShouldAddRole() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(RoleEntity.UserRole.ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponse);

         
        UserResponse result = userManagementService.addRoleToUser(userId, RoleEntity.UserRole.ADMIN);

         
        assertThat(result).isNotNull();
        assertThat(user.getRoles()).contains(adminRole);
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void addRoleToUser_WhenUserNotFound_ShouldThrowException() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> userManagementService.addRoleToUser(userId, RoleEntity.UserRole.ADMIN))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addRoleToUser_WhenRoleNotFound_ShouldThrowException() {
         
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(RoleEntity.UserRole.ADMIN)).thenReturn(Optional.empty());

         
        assertThatThrownBy(() -> userManagementService.addRoleToUser(userId, RoleEntity.UserRole.ADMIN))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void removeRoleFromUser_ShouldRemoveRole() {
         
        user.getRoles().add(adminRole);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(RoleEntity.UserRole.ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponse);

         
        UserResponse result = userManagementService.removeRoleFromUser(userId, RoleEntity.UserRole.ADMIN);

         
        assertThat(result).isNotNull();
        assertThat(user.getRoles()).doesNotContain(adminRole);
        verify(userRepository).saveAndFlush(user);
    }
}