package com.group12.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdateBillingSettingsRequest {
    @NotNull
    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double longRentHourRateMultiplier;

    @NotNull
    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double extraLongRentHourRateMultiplier;

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
}
