package com.example.bankcards.repository;

import com.example.bankcards.dto.response.CardProductResponse;
import com.example.bankcards.entity.CardProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardProductRepository extends JpaRepository<CardProductEntity, UUID> {

    Page<CardProductEntity> findAllByIsActive(Boolean isActive, Pageable pageable);
}
