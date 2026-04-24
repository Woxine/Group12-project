package com.group12.backend.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "billing")
public class BillingProperties {
    private BigDecimal longRentThresholdHours = new BigDecimal("24");
    private BigDecimal extraLongRentThresholdHours = new BigDecimal("72");
    private BigDecimal longRentHourRateMultiplier = new BigDecimal("0.85");
    private BigDecimal extraLongRentHourRateMultiplier = new BigDecimal("0.75");
    private Integer pendingPaymentLockMinutes = 5;

    public BigDecimal getLongRentThresholdHours() {
        return longRentThresholdHours;
    }

    public void setLongRentThresholdHours(BigDecimal longRentThresholdHours) {
        this.longRentThresholdHours = longRentThresholdHours;
    }

    public BigDecimal getExtraLongRentThresholdHours() {
        return extraLongRentThresholdHours;
    }

    public void setExtraLongRentThresholdHours(BigDecimal extraLongRentThresholdHours) {
        this.extraLongRentThresholdHours = extraLongRentThresholdHours;
    }

    public BigDecimal getLongRentHourRateMultiplier() {
        return longRentHourRateMultiplier;
    }

    public void setLongRentHourRateMultiplier(BigDecimal longRentHourRateMultiplier) {
        this.longRentHourRateMultiplier = longRentHourRateMultiplier;
    }

    public BigDecimal getExtraLongRentHourRateMultiplier() {
        return extraLongRentHourRateMultiplier;
    }

    public void setExtraLongRentHourRateMultiplier(BigDecimal extraLongRentHourRateMultiplier) {
        this.extraLongRentHourRateMultiplier = extraLongRentHourRateMultiplier;
    }

    public Integer getPendingPaymentLockMinutes() {
        return pendingPaymentLockMinutes;
    }

    public void setPendingPaymentLockMinutes(Integer pendingPaymentLockMinutes) {
        this.pendingPaymentLockMinutes = pendingPaymentLockMinutes;
    }
}
