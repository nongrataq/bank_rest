package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.request.CreateCardProductRequest;
import com.example.bankcards.dto.response.CardProductResponse;
import com.example.bankcards.entity.CardProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardProductMapper {

    CardProductEntity toEntity(CreateCardProductRequest request);

    CardProductResponse toDto(CardProductEntity entity);

}
