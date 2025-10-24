package com.finlab.gateway.service;

import com.finlab.gateway.controller.dto.LoginRequest;
import com.finlab.gateway.exception.InvalidCredentials;
import com.finlab.gateway.model.AuthToken;
import com.finlab.gateway.model.User;
import com.finlab.gateway.repository.AuthTokenRepository;
import com.finlab.gateway.repository.UserRepository;
import com.finlab.gateway.security.JwtResult;
import com.finlab.gateway.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final AuthTokenRepository authTokenRepository;


    public AuthService(UserRepository userRepository, JwtService jwtService, RedisTokenService redisTokenService, AuthTokenRepository authTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
        this.redisTokenService = redisTokenService;
        this.authTokenRepository = authTokenRepository;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentials("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentials("Invalid username or password");
        }


        JwtResult jwtResult = jwtService.generateToken(request.getUsername());
        String token = jwtResult.token();
        String jwtId = jwtResult.jwtId();
        Instant issuedAt = jwtResult.issuedAt();
        Instant expiresAt = jwtResult.expiresAt();

        redisTokenService.saveToken(jwtId, token, Duration.between(issuedAt, expiresAt));

        AuthToken authToken = new AuthToken();
        authToken.setUserId(user.getId());
        authToken.setJwtId(jwtId);
        authToken.setToken(token);
        authToken.setIssuedAt(issuedAt);
        authToken.setExpiresAt(expiresAt);
        authToken.setRevoked(false);

        authTokenRepository.save(authToken);

        revokePreviousTokens(user.getId());

        return token;
    }

    public void logout(String token) {
        token = token.substring(7);
        String jwtId = jwtService.extractJwtId(token);
        authTokenRepository.revokeToken(jwtId);
    }

    private void revokePreviousTokens(UUID userId) {
        List<AuthToken> activeTokens = authTokenRepository.findAllActiveByUserId(userId);
        for (AuthToken t : activeTokens) {
            t.setRevoked(true);
            authTokenRepository.revokeToken(t.getJwtId());
            redisTokenService.deleteToken(t.getJwtId());
        }
    }
}
