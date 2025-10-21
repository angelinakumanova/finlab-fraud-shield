package com.finlab.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1")
public class AccountsProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.accountsUrl}")
    private String accountsUrl;

    @GetMapping("/accounts/ping")
    public String pingAccounts() {
        String url = accountsUrl + "/api/v1/accounts/ping";
        try {
            String response = restTemplate.getForObject(url, String.class);
            return "Gateway reached Accounts: " + response;
        } catch (Exception e) {
            return "Error calling accounts-service: " + e.getMessage();
        }
    }
}
