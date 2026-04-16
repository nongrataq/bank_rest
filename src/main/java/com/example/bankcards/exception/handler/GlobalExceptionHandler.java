package com.example.bankcards.exception.handler;

import com.example.bankcards.exception.ExceptionMessage;
import com.example.bankcards.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ExceptionMessage> handleServiceException(ServiceException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ExceptionMessage.builder()
                                .exceptionName(e.getClass().getSimpleName())
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handleValidationException(MethodArgumentNotValidException e) {

        List<String> errors = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(error.getField() + ": " + error.getDefaultMessage())
        );

        e.getBindingResult().getGlobalErrors().forEach(error ->
                errors.add(error.getDefaultMessage())
        );

        ExceptionMessage message = new ExceptionMessage(
                "VALIDATION_ERROR",
                "Ошибка валидации запроса",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionMessage> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionMessage.builder()
                        .exceptionName(e.getClass().getSimpleName())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionMessage handeAllErrors(Exception e) {
        return ExceptionMessage.builder()
                .message(e.getMessage())
                .exceptionName(e.getClass().getSimpleName())
                .build();
    }


}
