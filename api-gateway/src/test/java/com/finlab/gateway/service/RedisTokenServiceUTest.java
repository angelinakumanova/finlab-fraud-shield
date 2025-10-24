package com.finlab.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisTokenServiceUTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisTokenService redisTokenService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void saveToken_shouldStoreTokenWithTTL() {
        // Arrange
        String username = "testUser";
        String token = "jwt-token";
        Duration duration = Duration.ofMinutes(10);

        // Act
        redisTokenService.saveToken(username, token, duration);

        // Assert
        verify(valueOperations).set("token:" + username, token, duration);
        verifyNoMoreInteractions(valueOperations);
    }

    @Test
    void getToken_shouldReturnTokenIfExists() {
        // Arrange
        String username = "john";
        String expectedToken = "abc123";
        when(valueOperations.get("token:" + username)).thenReturn(expectedToken);

        // Act
        Optional<String> result = redisTokenService.getToken(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedToken, result.get());
        verify(valueOperations).get("token:" + username);
    }

    @Test
    void getToken_shouldReturnEmptyIfNotFound() {
        // Arrange
        String username = "missing";
        when(valueOperations.get("token:" + username)).thenReturn(null);

        // Act
        Optional<String> result = redisTokenService.getToken(username);

        // Assert
        assertTrue(result.isEmpty());
        verify(valueOperations).get("token:" + username);
    }

    @Test
    void deleteToken_shouldDeleteKeyFromRedis() {
        // Arrange
        String username = "deleteMe";

        // Act
        redisTokenService.deleteToken(username);

        // Assert
        verify(redisTemplate).delete("token:" + username);
        verifyNoMoreInteractions(redisTemplate);
    }
}
