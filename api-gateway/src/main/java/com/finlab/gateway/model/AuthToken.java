package com.finlab.gateway.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AuthToken {
    private UUID id;
    private UUID userId;
    private String jwtId;
    private String token;
    private Instant issuedAt;
    private Instant expiresAt;
    private boolean revoked;

}