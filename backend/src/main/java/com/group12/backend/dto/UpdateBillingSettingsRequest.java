package com.group12.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class UpdateBillingSettingsRequest {
    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double longRentHourRateMultiplier;

    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double extraLongRentHourRateMultiplier;

    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double studentDiscountRate;

    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double seniorDiscountRate;

    @DecimalMin(value = "0.0001", inclusive = true)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double frequentDiscountRate;

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

    public Double getStudentDiscountRate() {
        return studentDiscountRate;
    }

    public void setStudentDiscountRate(Double studentDiscountRate) {
        this.studentDiscountRate = studentDiscountRate;
    }

    public Double getSeniorDiscountRate() {
        return seniorDiscountRate;
    }

    public void setSeniorDiscountRate(Double seniorDiscountRate) {
        this.seniorDiscountRate = seniorDiscountRate;
    }

    public Double getFrequentDiscountRate() {
        return frequentDiscountRate;
    }

    public void setFrequentDiscountRate(Double frequentDiscountRate) {
        this.frequentDiscountRate = frequentDiscountRate;
    }
}
