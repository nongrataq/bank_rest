package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<CardEntity, UUID> {
    Page<CardEntity> getAllByUser_Id(UUID id, Pageable pageable);

    Optional<CardEntity> findByIdAndUser_Id(UUID cardId, UUID userId);

    @Query("SELECT c FROM CardEntity c WHERE c.id = :id AND c.user.id = :userId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CardEntity> findByIdAndUser_IdWithLock(@Param("id") UUID id, @Param("userId") UUID userId);

    Page<CardEntity> findByCardStatus(CardEntity.CardStatus cardStatus, Pageable pageable);

    Page<CardEntity> findAllByUser_Id(UUID userId, Pageable pageable);

    Page<CardEntity> findAllByUser_IdAndCardStatus(UUID userId, CardEntity.CardStatus cardStatus, Pageable pageable);

    boolean existsByEncryptedPan(String encryptedPan);
}