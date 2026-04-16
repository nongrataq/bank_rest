package com.example.bankcards.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExceptionMessage(
        String message,
        String exceptionName,
        List<String> errors
) {
}
