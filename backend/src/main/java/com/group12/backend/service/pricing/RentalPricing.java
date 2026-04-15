package com.group12.backend.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.group12.backend.service.BillingRule;

public final class RentalPricing {
    private RentalPricing() {
    }

    public static BigDecimal computeTotal(BigDecimal hourRate, double durationHours, BillingRule rule) {
        if (hourRate == null || durationHours <= 0 || rule == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal duration = BigDecimal.valueOf(durationHours);
        BigDecimal t1 = rule.longRentThresholdHours();
        BigDecimal t2 = rule.extraLongRentThresholdHours();
        BigDecimal m1 = rule.longRentHourRateMultiplier();
        BigDecimal m2 = rule.extraLongRentHourRateMultiplier();
        if (t1 == null || t2 == null || m1 == null || m2 == null || t2.compareTo(t1) <= 0) {
            return hourRate.multiply(duration).setScale(2, RoundingMode.HALF_UP);
        }
        if (duration.compareTo(t1) <= 0) {
            return hourRate.multiply(duration).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal firstSegment = hourRate.multiply(t1);
        BigDecimal secondSegmentHours = duration.min(t2).subtract(t1);
        BigDecimal secondSegment = hourRate.multiply(m1).multiply(secondSegmentHours);
        BigDecimal thirdSegment = BigDecimal.ZERO;
        if (duration.compareTo(t2) > 0) {
            BigDecimal thirdSegmentHours = duration.subtract(t2);
            thirdSegment = hourRate.multiply(m2).multiply(thirdSegmentHours);
        }
        return firstSegment.add(secondSegment).add(thirdSegment).setScale(2, RoundingMode.HALF_UP);
    }
}
