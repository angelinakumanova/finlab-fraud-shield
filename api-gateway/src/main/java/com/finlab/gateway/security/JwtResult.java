package com.finlab.gateway.security;

import java.time.Instant;

public record JwtResult(String jwtId,
                        String token,
                        Instant issuedAt,
                        Instant expiresAt) {
}
