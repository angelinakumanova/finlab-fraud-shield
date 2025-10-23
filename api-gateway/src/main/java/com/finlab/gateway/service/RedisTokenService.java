package com.finlab.gateway.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String username, String token) {
        redisTemplate.opsForValue().set("token:" + username, token, Duration.ofHours(1));
    }

    public Optional<String> getToken(String username) {
        String token = redisTemplate.opsForValue().get("token:" + username);
        return Optional.ofNullable(token);
    }

    public void deleteToken(String username) {
        redisTemplate.delete("token:" + username);
    }
}
