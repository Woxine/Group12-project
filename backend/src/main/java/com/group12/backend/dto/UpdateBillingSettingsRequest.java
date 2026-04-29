package com.group12.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class UpdateBillingSettingsRequest {
    @DecimalMin(value = "0.0001", inclusive = true, message = "Long-rent multiplier (24h-72h) must be at least 0.0001.")
    @DecimalMax(value = "1.0", inclusive = true, message = "Long-rent multiplier (24h-72h) cannot be greater than 1.")
    private Double longRentHourRateMultiplier;

    @DecimalMin(value = "0.0001", inclusive = true, message = "Long-rent multiplier (>72h) must be at least 0.0001.")
    @DecimalMax(value = "1.0", inclusive = true, message = "Long-rent multiplier (>72h) cannot be greater than 1.")
    private Double extraLongRentHourRateMultiplier;

    @DecimalMin(value = "0.0001", inclusive = true, message = "Student discount rate must be at least 0.0001.")
    @DecimalMax(value = "1.0", inclusive = true, message = "Student discount rate cannot be greater than 1.")
    private Double studentDiscountRate;

    @DecimalMin(value = "0.0001", inclusive = true, message = "Senior discount rate must be at least 0.0001.")
    @DecimalMax(value = "1.0", inclusive = true, message = "Senior discount rate cannot be greater than 1.")
    private Double seniorDiscountRate;

    @DecimalMin(value = "0.0001", inclusive = true, message = "Frequent user discount rate must be at least 0.0001.")
    @DecimalMax(value = "1.0", inclusive = true, message = "Frequent user discount rate cannot be greater than 1.")
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
