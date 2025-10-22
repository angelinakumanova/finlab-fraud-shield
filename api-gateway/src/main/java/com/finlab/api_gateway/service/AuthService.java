package com.finlab.api_gateway.service;

import com.finlab.api_gateway.controller.dto.LoginRequest;
import com.finlab.api_gateway.exception.InvalidCredentials;
import com.finlab.api_gateway.model.User;
import com.finlab.api_gateway.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            throw new InvalidCredentials("Invalid username or password");
        }

        User user = optionalUser.get();
        boolean isValidPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isValidPassword) {
            throw new InvalidCredentials("Invalid username or password");
        }

    }
}
