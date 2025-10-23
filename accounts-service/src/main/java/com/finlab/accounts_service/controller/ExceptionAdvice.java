package com.finlab.accounts_service.controller;

import com.finlab.accounts_service.controller.dto.ErrorResponse;
import com.finlab.accounts_service.exception.InvalidIBAN;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(InvalidIBAN.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(InvalidIBAN ex, ServerWebExchange exchange) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(exchange.getRequest().getURI().getPath())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
