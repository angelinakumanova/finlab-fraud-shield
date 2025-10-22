package com.finlab.api_gateway.controller;

import com.finlab.api_gateway.controller.dto.LoginRequest;
import com.finlab.api_gateway.security.JwtService;
import com.finlab.api_gateway.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authService.login(request);
        String token = jwtService.generateToken(request.getUsername());

        return ResponseEntity.ok().header("Authorization", "Bearer " + token).build();
    }
}
