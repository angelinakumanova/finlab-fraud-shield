package com.finlab.accounts_service.service;

import com.finlab.accounts_service.exception.InvalidIBAN;
import com.finlab.accounts_service.repository.IbanRepository;
import com.finlab.accounts_service.repository.dto.IbanCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IbanServiceUTest {

    @Mock
    private IbanRepository ibanRepository;

    @InjectMocks
    private IbanService ibanService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // region checkIban()

    @Test
    void checkIban_shouldCallRepositoryAndReturnAllowDecision() {
        // Arrange
        String iban = "DE89370400440532013000";
        IbanCheckResponse expected = new IbanCheckResponse(
                iban, "ALLOW", 0.02, List.of("No previous reports", "Low transaction volume")
        );

        when(ibanRepository.findRiskForIban(iban)).thenReturn(expected);

        // Act
        IbanCheckResponse result = ibanService.checkIban(iban);

        // Assert
        assertEquals("ALLOW", result.decision());
        assertEquals(0.02, result.score());
        assertEquals(iban, result.iban());
        assertEquals(List.of("No previous reports", "Low transaction volume"), result.reasons());
        verify(ibanRepository).findRiskForIban(iban);
    }

    @Test
    void checkIban_shouldCallRepositoryAndReturnBlockDecision() {
        String iban = "GB82WEST12345698765432";
        IbanCheckResponse expected = new IbanCheckResponse(
                iban, "BLOCK", 0.99, List.of("Reported multiple times", "High risk country")
        );

        when(ibanRepository.findRiskForIban(iban)).thenReturn(expected);

        IbanCheckResponse result = ibanService.checkIban(iban);

        assertEquals("BLOCK", result.decision());
        assertEquals(0.99, result.score());
        assertEquals(iban, result.iban());
        verify(ibanRepository).findRiskForIban(iban);
    }

    @Test
    void checkIban_shouldTrimWhitespaceAndReturnReviewDecision() {
        // Arrange
        String ibanWithSpaces = "   FR7630006000011234567890189   ";
        String trimmed = "FR7630006000011234567890189";
        IbanCheckResponse expected = new IbanCheckResponse(
                trimmed, "REVIEW", 0.55, List.of("Unusual transaction pattern")
        );

        when(ibanRepository.findRiskForIban(trimmed)).thenReturn(expected);

        // Act
        IbanCheckResponse result = ibanService.checkIban(ibanWithSpaces);

        // Assert
        assertEquals("REVIEW", result.decision());
        assertEquals(0.55, result.score());
        assertEquals(trimmed, result.iban());
        verify(ibanRepository).findRiskForIban(trimmed);
    }

    @Test
    void checkIban_shouldThrowInvalidIBAN_whenNullOrBlank() {
        assertThrows(InvalidIBAN.class, () -> ibanService.checkIban(null));
        assertThrows(InvalidIBAN.class, () -> ibanService.checkIban(""));
        assertThrows(InvalidIBAN.class, () -> ibanService.checkIban("  "));
        verifyNoInteractions(ibanRepository);
    }

    // endregion

    // region reportIban()

    @Test
    void reportIban_shouldInsertReportAndEdge_whenRelatedProvided() {
        String iban = "GB82WEST12345698765432";
        String related = "GB12TEST12345698765432";
        String reason = "Suspicious transactions";
        String username = "alice";

        ibanService.reportIban(iban, related, reason, username);

        verify(ibanRepository).insertReport(eq(iban), eq(reason), anyString());
        verify(ibanRepository).insertEdge(eq(iban), eq(related));
    }

    @Test
    void reportIban_shouldNotInsertEdge_whenRelatedIbanIsBlankOrNull() {
        String iban = "GB82WEST12345698765432";
        String reason = "Fraudulent activity";
        String username = "bob";

        ibanService.reportIban(iban, null, reason, username);
        ibanService.reportIban(iban, " ", reason, username);

        verify(ibanRepository, times(2)).insertReport(eq(iban), eq(reason), anyString());
        verify(ibanRepository, never()).insertEdge(anyString(), anyString());
    }

    @Test
    void reportIban_shouldThrowInvalidIBAN_whenMainIbanIsNullOrBlank() {
        assertThrows(InvalidIBAN.class, () -> ibanService.reportIban(null, "REL", "reason", "user"));
        assertThrows(InvalidIBAN.class, () -> ibanService.reportIban(" ", "REL", "reason", "user"));
        verifyNoInteractions(ibanRepository);
    }

    // endregion

    // region hashReporter()

    @Test
    void hashReporter_shouldReturnDeterministicSha256Hash() throws Exception {
        Method hashMethod = IbanService.class.getDeclaredMethod("hashReporter", String.class);
        hashMethod.setAccessible(true);

        String username = "testUser";
        String hash1 = (String) hashMethod.invoke(ibanService, username);
        String hash2 = (String) hashMethod.invoke(ibanService, username);

        assertNotNull(hash1);
        assertEquals(64, hash1.length()); // SHA-256 -> 64 hex chars
        assertEquals(hash1, hash2);
    }

    // endregion
}
