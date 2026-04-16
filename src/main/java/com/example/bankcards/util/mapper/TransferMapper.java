package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.request.TransactionRequest;
import com.example.bankcards.dto.response.TransactionElementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferMapper {

    TransactionElementResponse toResponse(TransactionRequest request);
}
