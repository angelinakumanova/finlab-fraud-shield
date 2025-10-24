package com.finlab.accounts_service.controller;

import com.finlab.accounts_service.controller.dto.IbanCheckRequest;
import com.finlab.accounts_service.controller.dto.IbanReportRequest;
import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import com.finlab.accounts_service.service.IbanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/iban")
public class IbanController {

    private final IbanService ibanService;

    public IbanController(IbanService ibanService) {
        this.ibanService = ibanService;
    }

    @PostMapping("/check")
    public ResponseEntity<IbanCheckResponse> check(@RequestBody IbanCheckRequest req) {
        IbanCheckResponse resp = ibanService.checkIban(req.iban().trim());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/report")
    public ResponseEntity<?> report(
            @RequestHeader("X-User") String username,
            @RequestBody IbanReportRequest req) {


        ibanService.reportIban(req.iban(), req.relatedIban(), req.reason(), username);

        return ResponseEntity.ok(
                Map.of("OK", "IBAN reported successfully")
        );
    }
}
