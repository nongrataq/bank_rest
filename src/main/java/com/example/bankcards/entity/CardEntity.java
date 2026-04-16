package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "cards")
public class CardEntity extends BaseEntity {

    @Column(name = "masked_pan", nullable = false)
    private String maskedPan;

    @Column(name = "encrypted_pan", nullable = false)
    private String encryptedPan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Column(precision = 19, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_product_id")
    private CardProductEntity cardProduct;

    public enum CardStatus {
        ACTIVE,
        BLOCKED,
        EXPIRED
    }

    public boolean isActive() {
        return this.getCardStatus().equals(CardStatus.ACTIVE);
    }
}
