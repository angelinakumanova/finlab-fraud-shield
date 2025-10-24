package com.finlab.accounts_service.service;

import com.finlab.accounts_service.exception.InvalidIBAN;
import com.finlab.accounts_service.repository.IbanRepository;
import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class IbanService {

    private final IbanRepository ibanRepository;

    public IbanService(IbanRepository ibanRepository) {
        this.ibanRepository = ibanRepository;
    }

    public IbanCheckResponse checkIban(String iban) {
        if (iban == null || iban.isBlank()) {
            throw new InvalidIBAN("Iban cannot be empty");
        }

        return ibanRepository.findRiskForIban(iban.trim());
    }

    public void reportIban(String iban, String relatedIban, String reason, String username) {
        if (iban == null || iban.isBlank()) {
            throw new InvalidIBAN("IBAN cannot be empty");
        }

        String reporterHash = hashReporter(username);
        ibanRepository.insertReport(iban.trim(), reason, reporterHash);

        if (relatedIban != null && !relatedIban.isBlank()) {
            ibanRepository.insertEdge(iban.trim(), relatedIban.trim());
        }


    }

    private String hashReporter(String username) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(username.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();

            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
