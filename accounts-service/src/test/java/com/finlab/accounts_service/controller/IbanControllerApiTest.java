package com.finlab.accounts_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finlab.accounts_service.controller.dto.IbanCheckRequest;
import com.finlab.accounts_service.controller.dto.IbanReportRequest;
import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import com.finlab.accounts_service.security.ApiFilter;
import com.finlab.accounts_service.service.IbanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = IbanController.class,
        excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = ApiFilter.class
))
public class IbanControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IbanService ibanService;

    @Autowired
    private ObjectMapper objectMapper;

    // region /check

    @Test
    void check_shouldReturnIbanCheckResponse_whenValidRequest() throws Exception {
        // Arrange
        String iban = "DE89370400440532013000";
        IbanCheckResponse response = new IbanCheckResponse(
                iban,
                "ALLOW",
                0.12,
                List.of("No previous reports")
        );

        Mockito.when(ibanService.checkIban(iban)).thenReturn(response);

        IbanCheckRequest request = new IbanCheckRequest(iban);

        // Act & Assert
        mockMvc.perform(post("/api/v1/iban/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(iban))
                .andExpect(jsonPath("$.decision").value("ALLOW"))
                .andExpect(jsonPath("$.score").value(0.12))
                .andExpect(jsonPath("$.reasons[0]").value("No previous reports"));

        Mockito.verify(ibanService).checkIban(iban);
    }

    // endregion

    // region /report

    @Test
    void report_shouldReturnSuccessMessage_whenRequestValid() throws Exception {
        // Arrange
        IbanReportRequest request = new IbanReportRequest(
                "GB82WEST12345698765432",
                "GB12TEST12345698765432",
                "Suspicious transaction"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/iban/report")
                        .header("X-User", "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.OK").value("IBAN reported successfully"));

        Mockito.verify(ibanService).reportIban(
                eq("GB82WEST12345698765432"),
                eq("GB12TEST12345698765432"),
                eq("Suspicious transaction"),
                eq("alice")
        );
    }

    // endregion
}
