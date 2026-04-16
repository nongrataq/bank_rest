package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardBlockRepository extends JpaRepository<CardBlockRequestEntity, UUID> {
    Page<CardBlockRequestEntity> findAllByStatus(CardBlockRequestEntity.RequestStatus status, Pageable pageable);

    Optional<CardBlockRequestEntity> findByCard_Id(UUID id);
}
