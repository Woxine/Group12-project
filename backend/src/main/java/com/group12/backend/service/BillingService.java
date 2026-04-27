package com.group12.backend.service;

import java.math.BigDecimal;
import java.util.List;

import com.group12.backend.entity.BillingSettingsLog;

public interface BillingService {
    BillingRule getCurrentRule();

    BillingRule updateSettings(
            BigDecimal longRentHourRateMultiplier,
            BigDecimal extraLongRentHourRateMultiplier,
            BigDecimal studentDiscountRate,
            BigDecimal seniorDiscountRate,
            BigDecimal frequentDiscountRate,
            Long operatorUserId);

    List<BillingSettingsLog> getRecentLogs(int limit);
}
