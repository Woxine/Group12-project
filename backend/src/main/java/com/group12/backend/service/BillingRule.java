package com.group12.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillingRule(
        BigDecimal longRentThresholdHours,
        BigDecimal extraLongRentThresholdHours,
        BigDecimal longRentHourRateMultiplier,
        BigDecimal extraLongRentHourRateMultiplier,
        BigDecimal studentDiscountRate,
        BigDecimal seniorDiscountRate,
        BigDecimal frequentDiscountRate,
        LocalDateTime updatedAt
) {
}
