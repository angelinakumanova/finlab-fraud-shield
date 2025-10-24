package com.finlab.gateway.service;

import com.finlab.gateway.controller.dto.LoginRequest;
import com.finlab.gateway.exception.InvalidCredentials;
import com.finlab.gateway.model.AuthToken;
import com.finlab.gateway.model.User;
import com.finlab.gateway.repository.AuthTokenRepository;
import com.finlab.gateway.repository.UserRepository;
import com.finlab.gateway.security.JwtResult;
import com.finlab.gateway.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<AuthToken> authTokenCaptor;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // region LOGIN TESTS

    @Test
    void login_success_shouldReturnJwtToken_andSaveEverythingProperly() {
        // Arrange
        String username = "testUser";
        String password = "pass123";
        String hashedPassword = encoder.encode(password);
        String jwtId = UUID.randomUUID().toString();
        String token = "jwt-token-123";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(hashedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(username)).thenReturn(
                new JwtResult(jwtId, token, Instant.now(), Instant.now().plusSeconds(3600))
        );
        when(authTokenRepository.findAllActiveByUserId(user.getId())).thenReturn(List.of());

        // Act
        String result = authService.login(new LoginRequest(username, password));

        // Assert
        assertNotNull(result);
        assertFalse(result.isBlank());
        assertEquals(token, result, "Returned token must match mocked JwtResult.token()");
        verify(jwtService).generateToken(username);
        verify(redisTokenService).saveToken(eq(jwtId), eq(token), any(Duration.class));
        verify(authTokenRepository).save(any(AuthToken.class));
    }


    @Test
    void login_shouldThrowInvalidCredentials_whenUserNotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("notfound", "any");

        assertThrows(InvalidCredentials.class, () -> authService.login(request));

        verify(userRepository).findByUsername("notfound");
        verifyNoInteractions(jwtService, redisTokenService, authTokenRepository);
    }

    @Test
    void login_shouldThrowInvalidCredentials_whenPasswordDoesNotMatch() {
        User user = new User();
        user.setUsername("test");
        user.setPassword(encoder.encode("correct"));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        LoginRequest wrongPassword = new LoginRequest("test", "wrongpass");

        assertThrows(InvalidCredentials.class, () -> authService.login(wrongPassword));

        verify(userRepository).findByUsername("test");
        verifyNoInteractions(jwtService, redisTokenService, authTokenRepository);
    }

    @Test
    void login_shouldRevokePreviousTokens() {
        String username = "user";
        String password = "pwd";
        String hashed = encoder.encode(password);
        String jwtId = UUID.randomUUID().toString();
        String token = "token-xyz";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(hashed);

        AuthToken oldToken = new AuthToken();
        oldToken.setJwtId(UUID.randomUUID().toString());
        oldToken.setUserId(user.getId());
        oldToken.setRevoked(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(username)).thenReturn(
                new JwtResult(token, jwtId, Instant.now(), Instant.now().plusSeconds(3600))
        );
        when(authTokenRepository.findAllActiveByUserId(user.getId())).thenReturn(List.of(oldToken));

        // Act
        authService.login(new LoginRequest(username, password));

        // Assert
        verify(authTokenRepository).findAllActiveByUserId(user.getId());
        verify(authTokenRepository).revokeToken(oldToken.getJwtId());
        verify(redisTokenService).deleteToken(oldToken.getJwtId());
    }

    // endregion

    // region LOGOUT TESTS

    @Test
    void logout_shouldRevokeToken() {
        String rawToken = "Bearer abc.def.ghi";
        String jwtId = "jwt-id-123";

        when(jwtService.extractJwtId("abc.def.ghi")).thenReturn(jwtId);

        authService.logout(rawToken);

        verify(jwtService).extractJwtId("abc.def.ghi");
        verify(authTokenRepository).revokeToken(jwtId);
    }

    // endregion

    // region PRIVATE METHOD (Indirectly tested via login)

    @Test
    void revokePreviousTokens_shouldRevokeAndDeleteAllActiveTokens() {
        UUID userId = UUID.randomUUID();
        AuthToken t1 = new AuthToken();
        t1.setJwtId("jwt1");
        AuthToken t2 = new AuthToken();
        t2.setJwtId("jwt2");

        when(authTokenRepository.findAllActiveByUserId(userId)).thenReturn(List.of(t1, t2));

        // Call indirectly via reflection (for isolation testing)
        // Not strictly needed, but useful for coverage
        assertDoesNotThrow(() -> {
            var method = AuthService.class.getDeclaredMethod("revokePreviousTokens", UUID.class);
            method.setAccessible(true);
            method.invoke(authService, userId);
        });

        verify(authTokenRepository).revokeToken("jwt1");
        verify(authTokenRepository).revokeToken("jwt2");
        verify(redisTokenService).deleteToken("jwt1");
        verify(redisTokenService).deleteToken("jwt2");
    }

    // endregion
}
