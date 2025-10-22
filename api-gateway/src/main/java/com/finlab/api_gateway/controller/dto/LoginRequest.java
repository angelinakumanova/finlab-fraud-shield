package com.finlab.api_gateway.controller.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;
}
