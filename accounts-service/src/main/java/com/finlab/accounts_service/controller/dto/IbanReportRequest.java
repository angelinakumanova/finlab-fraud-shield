package com.finlab.accounts_service.controller.dto;

public record IbanReportRequest(String iban, String relatedIban, String reason) {}
