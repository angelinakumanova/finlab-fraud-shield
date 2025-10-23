package com.finlab.gateway.controller.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
}
