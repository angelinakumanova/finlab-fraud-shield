package com.finlab.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceUTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Use a fixed, valid 256-bit secret (base64-encoded)
        String secretKey = Base64.getEncoder().encodeToString("this_is_a_very_secure_key_123456".getBytes());
        ReflectionTestUtils.setField(jwtService, "secretB64", secretKey);
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", 3600L);
    }

    @Test
    void generateToken_shouldProduceValidJwt_andReturnExpectedFields() {
        // Act
        JwtResult result = jwtService.generateToken("alice");

        // Assert
        assertNotNull(result);
        assertNotNull(result.token());
        assertNotNull(result.jwtId());
        assertFalse(result.jwtId().isBlank());
        assertTrue(result.token().contains(".")); // JWT structure: header.payload.signature
        assertTrue(result.expiresAt().isAfter(result.issuedAt()));
        assertEquals(3600, result.expiresAt().getEpochSecond() - result.issuedAt().getEpochSecond(), 2);
    }

    @Test
    void parseToken_shouldReturnSameClaimsUsedForGeneration() {
        // Arrange
        JwtResult generated = jwtService.generateToken("bob");

        // Act
        Jws<Claims> parsed = jwtService.parseToken(generated.token());

        // Assert
        assertEquals("bob", parsed.getPayload().getSubject());
        assertEquals(generated.jwtId(), parsed.getPayload().getId());
        assertNotNull(parsed.getPayload().getIssuedAt());
        assertNotNull(parsed.getPayload().getExpiration());
        assertTrue(parsed.getPayload().getExpiration().after(parsed.getPayload().getIssuedAt()));
    }

    @Test
    void extractJwtId_shouldReturnJtiFromToken() {
        // Arrange
        JwtResult generated = jwtService.generateToken("charlie");

        // Act
        String extracted = jwtService.extractJwtId(generated.token());

        // Assert
        assertEquals(generated.jwtId(), extracted);
    }

    @Test
    void parseToken_shouldFailForInvalidSignature() {
        // Arrange
        JwtResult generated = jwtService.generateToken("dave");

        // Tamper the token (simulate corruption)
        String tampered = generated.token().substring(0, generated.token().length() - 2) + "aa";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtService.parseToken(tampered));
    }

    @Test
    void parseToken_shouldFailForMalformedToken() {
        // Arrange
        String malformed = "not_a_real_jwt";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtService.parseToken(malformed));
    }
}
