package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Table(name = "roles")
public class RoleEntity extends BaseEntity {

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "USER")
    private UserRole role;

    public enum UserRole {
        USER,
        ADMIN
    }
}