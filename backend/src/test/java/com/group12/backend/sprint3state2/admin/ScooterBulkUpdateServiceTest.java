package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.BulkScooterUpdateRequest;
import com.group12.backend.dto.ScooterBulkApplyResponse;
import com.group12.backend.dto.ScooterBulkPreviewResponse;
import com.group12.backend.entity.Scooter;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.LocationPointRepository;
import com.group12.backend.repository.ScooterRepository;
import com.group12.backend.service.BillingService;
import com.group12.backend.service.impl.ScooterServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Scooter bulk update service")
class ScooterBulkUpdateServiceTest {

    @Mock
    private ScooterRepository scooterRepository;
    @Mock
    private BillingService billingService;
    @Mock
    private LocationPointRepository locationPointRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ScooterServiceImpl scooterService;

    @Test
    @DisplayName("previewBulkUpdateByType_returnsStatusBreakdownAndRiskFlags")
    void previewBulkUpdateByType_returnsStatusBreakdownAndRiskFlags() {
        BulkScooterUpdateRequest request = new BulkScooterUpdateRequest();
        request.setType("GEN1");
        request.setVisible(false);

        when(scooterRepository.findByType("GEN1")).thenReturn(List.of(
                scooter(1L, "GEN1", "AVAILABLE", "3.5", true),
                scooter(2L, "GEN1", "RENTED", "3.5", true),
                scooter(3L, "GEN1", "MAINTENANCE", "3.5", false)));

        ScooterBulkPreviewResponse result = scooterService.previewBulkUpdateByType(request);

        assertThat(result.getType()).isEqualTo("GEN1");
        assertThat(result.getMatchedCount()).isEqualTo(3);
        assertThat(result.getHiddenCount()).isEqualTo(1);
        assertThat(result.isRisky()).isTrue();
        assertThat(result.getStatusBreakdown().get("AVAILABLE")).isEqualTo(1);
        assertThat(result.getStatusBreakdown().get("RENTED")).isEqualTo(1);
        assertThat(result.getRiskWarnings()).isNotEmpty();
    }

    @Test
    @DisplayName("applyBulkUpdateByType_requiresConfirmWhenRisky")
    void applyBulkUpdateByType_requiresConfirmWhenRisky() {
        BulkScooterUpdateRequest request = new BulkScooterUpdateRequest();
        request.setType("GEN2");
        request.setStatus("MAINTENANCE");

        when(scooterRepository.findByType("GEN2")).thenReturn(List.of(
                scooter(10L, "GEN2", "AVAILABLE", "4.0", true)));

        assertThatThrownBy(() -> scooterService.applyBulkUpdateByType(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("confirm_risky");

        verify(scooterRepository, never()).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("applyBulkUpdateByType_updatesOnlyMatchingTypeRows")
    void applyBulkUpdateByType_updatesOnlyMatchingTypeRows() {
        BulkScooterUpdateRequest request = new BulkScooterUpdateRequest();
        request.setType("GEN3");
        request.setHour_rate(new BigDecimal("5.5"));
        request.setVisible(true);
        request.setConfirm_risky(true);

        Scooter match = scooter(21L, "GEN3", "AVAILABLE", "3.0", false);
        Scooter mismatch = scooter(22L, "GEN2", "AVAILABLE", "3.0", false);
        when(scooterRepository.findByType("GEN3")).thenReturn(List.of(match, mismatch));

        ScooterBulkApplyResponse result = scooterService.applyBulkUpdateByType(request);

        ArgumentCaptor<List<Scooter>> captor = ArgumentCaptor.forClass(List.class);
        verify(scooterRepository).saveAll(captor.capture());
        List<Scooter> saved = captor.getValue();

        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getId()).isEqualTo(21L);
        assertThat(saved.get(0).getHourRate()).isEqualByComparingTo("5.5");
        assertThat(saved.get(0).getVisible()).isTrue();
        assertThat(mismatch.getHourRate()).isEqualByComparingTo("3.0");
        assertThat(mismatch.getVisible()).isFalse();
        assertThat(result.getMatchedCount()).isEqualTo(1);
        assertThat(result.getUpdatedCount()).isEqualTo(1);
    }

    private static Scooter scooter(Long id, String type, String status, String rate, Boolean visible) {
        Scooter scooter = new Scooter();
        scooter.setId(id);
        scooter.setType(type);
        scooter.setStatus(status);
        scooter.setHourRate(new BigDecimal(rate));
        scooter.setVisible(visible);
        return scooter;
    }
}
