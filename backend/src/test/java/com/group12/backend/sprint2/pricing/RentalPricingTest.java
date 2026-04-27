package com.group12.backend.sprint2.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.service.BillingRule;
import com.group12.backend.service.pricing.RentalPricing;

@DisplayName("RentalPricing")
class RentalPricingTest {

    private static final BillingRule RULE = new BillingRule(
            new BigDecimal("24"),
            new BigDecimal("72"),
            new BigDecimal("0.85"),
            new BigDecimal("0.75"),
            new BigDecimal("0.80"),
            new BigDecimal("0.80"),
            new BigDecimal("0.80"),
            null);

    @Test
    @DisplayName("uses base hourly rate when duration <= 24h")
    void computeTotal_usesLinearRate_beforeFirstThreshold() {
        BigDecimal total = RentalPricing.computeTotal(new BigDecimal("3.50"), 4.0, RULE);
        assertThat(total).isEqualByComparingTo("14.00");
    }

    @Test
    @DisplayName("uses first long-rent multiplier between 24h and 72h")
    void computeTotal_usesFirstMultiplier_betweenThresholds() {
        BigDecimal total = RentalPricing.computeTotal(new BigDecimal("3.50"), 48.0, RULE);
        // 24*3.5 + 24*3.5*0.85 = 155.40
        assertThat(total).isEqualByComparingTo("155.40");
    }

    @Test
    @DisplayName("uses second long-rent multiplier after 72h")
    void computeTotal_usesSecondMultiplier_afterSecondThreshold() {
        BigDecimal total = RentalPricing.computeTotal(new BigDecimal("3.50"), 100.0, RULE);
        // 24*3.5 + 48*3.5*0.85 + 28*3.5*0.75 = 301.70
        assertThat(total).isEqualByComparingTo("301.70");
    }

    @Test
    @DisplayName("is exact on threshold boundaries")
    void computeTotal_respectsThresholdBoundaries() {
        BigDecimal at24 = RentalPricing.computeTotal(new BigDecimal("3.50"), 24.0, RULE);
        BigDecimal at72 = RentalPricing.computeTotal(new BigDecimal("3.50"), 72.0, RULE);
        assertThat(at24).isEqualByComparingTo("84.00");
        assertThat(at72).isEqualByComparingTo("226.80");
    }

    @Test
    @DisplayName("supports extension crossing threshold by using total hours")
    void computeTotal_handlesExtensionAcrossThreshold() {
        BigDecimal before = RentalPricing.computeTotal(new BigDecimal("3.50"), 71.0, RULE);
        BigDecimal after = RentalPricing.computeTotal(new BigDecimal("3.50"), 73.0, RULE);
        assertThat(before).isEqualByComparingTo("223.83");
        assertThat(after).isEqualByComparingTo("229.43");
    }
}
