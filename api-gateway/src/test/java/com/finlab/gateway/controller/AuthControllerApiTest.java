package com.finlab.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finlab.gateway.controller.dto.LoginRequest;
import com.finlab.gateway.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_shouldReturnJwtToken_whenCredentialsValid() throws Exception {
        // Arrange
        String token = "jwt-12345";
        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(token);

        LoginRequest request = new LoginRequest("alice", "password");

        // Act + Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        Mockito.verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void logout_shouldReturnSuccessMessage() throws Exception {
        // Arrange
        String bearerToken = "Bearer jwt-123";

        // Act + Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully logged out"));

        Mockito.verify(authService).logout(eq(bearerToken));
    }
}
