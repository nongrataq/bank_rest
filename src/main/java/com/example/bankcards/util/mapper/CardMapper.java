package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.request.CardBlockRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardBlockResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardBlockRequestEntity;
import com.example.bankcards.entity.CardEntity;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {

    CardEntity toEntity(CardRequest dto);

    CardBlockRequestEntity toEntity(CardBlockRequest request);

    @Mapping(source = "maskedPan", target = "lastFour", qualifiedByName = "lastFour")
    @Mapping(source = "user.username", target = "cardholderName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "expiryDate", target = "expiryDate", qualifiedByName = "formatExpiryDate")
    CardResponse toDto(CardEntity entity);

    @Mapping(source = "entity.requestedBy.id", target = "requestedBy")
    @Mapping(source = "entity.card.id", target = "cardId")
    @Mapping(source = "createdAt", target = "createdAt")
    CardBlockResponse toDto(CardBlockRequestEntity entity);

    List<CardResponse> toDto(List<CardEntity> entities);

    @Named("lastFour")
    default String getLastFourPan(String pan) {
        return pan.substring(pan.length() - 4);
    }

    @Named("formatExpiryDate")
    default String formatExpiryDate(LocalDate date) {
        if (date == null) return null;
        return String.format("%02d/%d", date.getMonthValue(), date.getYear() % 100);
    }
}
