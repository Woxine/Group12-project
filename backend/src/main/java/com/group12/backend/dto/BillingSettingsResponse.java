package com.group12.backend.dto;

import java.time.LocalDateTime;

public class BillingSettingsResponse {
    private Double longRentThresholdHours;
    private Double extraLongRentThresholdHours;
    private Double longRentHourRateMultiplier;
    private Double extraLongRentHourRateMultiplier;
    private LocalDateTime updatedAt;

    public Double getLongRentThresholdHours() {
        return longRentThresholdHours;
    }

    public void setLongRentThresholdHours(Double longRentThresholdHours) {
        this.longRentThresholdHours = longRentThresholdHours;
    }

    public Double getExtraLongRentThresholdHours() {
        return extraLongRentThresholdHours;
    }

    public void setExtraLongRentThresholdHours(Double extraLongRentThresholdHours) {
        this.extraLongRentThresholdHours = extraLongRentThresholdHours;
    }

    public Double getLongRentHourRateMultiplier() {
        return longRentHourRateMultiplier;
    }

    public void setLongRentHourRateMultiplier(Double longRentHourRateMultiplier) {
        this.longRentHourRateMultiplier = longRentHourRateMultiplier;
    }

    public Double getExtraLongRentHourRateMultiplier() {
        return extraLongRentHourRateMultiplier;
    }

    public void setExtraLongRentHourRateMultiplier(Double extraLongRentHourRateMultiplier) {
        this.extraLongRentHourRateMultiplier = extraLongRentHourRateMultiplier;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
