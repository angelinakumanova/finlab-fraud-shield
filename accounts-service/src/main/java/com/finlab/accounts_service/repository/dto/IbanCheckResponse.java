package com.finlab.accounts_service.repository.dto;


import java.util.List;

public record IbanCheckResponse(String iban, String decision, double score, List<String> reasons) {}

