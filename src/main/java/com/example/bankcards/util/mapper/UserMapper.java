package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.RoleEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.security.details.CustomUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserEntity toEntity(UserRequest dto);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToStrings")
    UserResponse toDto(UserEntity entity);

    List<UserResponse> toDto(List<UserEntity> entities);

    UserEntity toEntity(CustomUserDetails user);

    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<RoleEntity> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.toSet());
    }
}