package com.finlab.gateway.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String username;
    private String password;
    private LocalDateTime createdAt;
}
