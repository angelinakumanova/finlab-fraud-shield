package com.finlab.accounts_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "Accounts service OK";
    }
}
