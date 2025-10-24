package com.finlab.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.jwt.secret-b64}")
    private String secretB64;

    @Value("${app.jwt.ttl-seconds}")
    private long ttlSeconds;

    private SecretKey getKey() {
        byte[] decoded = Base64.getDecoder().decode(secretB64);
        return Keys.hmacShaKeyFor(decoded);
    }

    public JwtResult generateToken(String username) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlSeconds);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .id(jti)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(getKey())
                .compact();

        return new JwtResult(jti, token, now, expiresAt);
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token);
    }

    public String extractJwtId(String token) {
        return parseToken(token).getPayload().getId();
    }
}
