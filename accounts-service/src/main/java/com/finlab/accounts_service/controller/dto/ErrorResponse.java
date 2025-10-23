package com.finlab.accounts_service.controller.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse (int status, String message, String path, LocalDateTime timestamp) {}
