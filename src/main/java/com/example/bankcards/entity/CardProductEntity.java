package com.example.bankcards.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "card_products")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardProductEntity extends BaseEntity {

    @Column(name = "card_name", unique = true, nullable = false)
    private String cardName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "cardProduct")
    private Set<CardEntity> cards;
}


