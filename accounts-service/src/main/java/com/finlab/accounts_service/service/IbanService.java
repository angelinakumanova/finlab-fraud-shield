package com.finlab.accounts_service.service;

import com.finlab.accounts_service.exception.InvalidIBAN;
import com.finlab.accounts_service.repository.IbanRepository;
import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import org.springframework.stereotype.Service;

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
}
