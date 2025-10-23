package com.finlab.gateway.service;

import com.finlab.gateway.controller.dto.LoginRequest;
import com.finlab.gateway.exception.InvalidCredentials;
import com.finlab.gateway.model.User;
import com.finlab.gateway.repository.UserRepository;
import com.finlab.gateway.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentials("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentials("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());



        return token;
    }
}
