package com.finlab.gateway.controller;

import com.finlab.gateway.controller.dto.LoginRequest;
import com.finlab.gateway.security.JwtService;
import com.finlab.gateway.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        return ResponseEntity.ok()
                .body(Map.of("token", token));
    }
}
