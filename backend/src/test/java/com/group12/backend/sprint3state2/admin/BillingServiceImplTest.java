package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.config.BillingProperties;
import com.group12.backend.config.DiscountProperties;
import com.group12.backend.entity.BillingSettings;
import com.group12.backend.repository.BillingSettingsLogRepository;
import com.group12.backend.repository.BillingSettingsRepository;
import com.group12.backend.service.BillingRule;
import com.group12.backend.service.impl.BillingServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingServiceImpl")
class BillingServiceImplTest {

    @Mock
    private BillingSettingsRepository billingSettingsRepository;
    @Mock
    private BillingSettingsLogRepository billingSettingsLogRepository;
    @Mock
    private BillingProperties billingProperties;
    @Mock
    private DiscountProperties discountProperties;

    @InjectMocks
    private BillingServiceImpl billingService;

    @Test
    @DisplayName("getCurrentRule_createsDefaultRatesForAllDiscountTypes")
    void getCurrentRule_createsDefaultRatesForAllDiscountTypes() {
        when(billingSettingsRepository.findById(1L)).thenReturn(Optional.empty());
        when(discountProperties.getRate()).thenReturn(new BigDecimal("0.82"));
        when(billingSettingsRepository.save(any(BillingSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BillingRule rule = billingService.getCurrentRule();

        assertThat(rule.studentDiscountRate()).isEqualByComparingTo("0.82");
        assertThat(rule.seniorDiscountRate()).isEqualByComparingTo("0.82");
        assertThat(rule.frequentDiscountRate()).isEqualByComparingTo("0.82");
    }

    @Test
    @DisplayName("updateSettings_allowsSingleDiscountRatePatch")
    void updateSettings_allowsSingleDiscountRatePatch() {
        BillingSettings existing = new BillingSettings();
        existing.setId(1L);
        existing.setLongRentThresholdHours(new BigDecimal("24"));
        existing.setExtraLongRentThresholdHours(new BigDecimal("72"));
        existing.setLongRentMultiplier(new BigDecimal("0.85"));
        existing.setExtraLongRentMultiplier(new BigDecimal("0.75"));
        existing.setStudentDiscountRate(new BigDecimal("0.80"));
        existing.setSeniorDiscountRate(new BigDecimal("0.80"));
        existing.setFrequentDiscountRate(new BigDecimal("0.80"));

        when(billingSettingsRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(billingSettingsRepository.save(any(BillingSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BillingRule updated = billingService.updateSettings(
                null,
                null,
                new BigDecimal("0.70"),
                null,
                null,
                9L);

        assertThat(updated.studentDiscountRate()).isEqualByComparingTo("0.70");
        assertThat(updated.seniorDiscountRate()).isEqualByComparingTo("0.80");
        assertThat(updated.frequentDiscountRate()).isEqualByComparingTo("0.80");
        verify(billingSettingsLogRepository, never()).save(any());
    }
}
