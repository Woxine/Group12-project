package com.group12.backend.dto;

import java.time.LocalDateTime;

public class BillingSettingsLogResponse {
    private Long id;
    private Double oldLongRentHourRateMultiplier;
    private Double newLongRentHourRateMultiplier;
    private Double oldExtraLongRentHourRateMultiplier;
    private Double newExtraLongRentHourRateMultiplier;
    private Long operatorUserId;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getOldLongRentHourRateMultiplier() {
        return oldLongRentHourRateMultiplier;
    }

    public void setOldLongRentHourRateMultiplier(Double oldLongRentHourRateMultiplier) {
        this.oldLongRentHourRateMultiplier = oldLongRentHourRateMultiplier;
    }

    public Double getNewLongRentHourRateMultiplier() {
        return newLongRentHourRateMultiplier;
    }

    public void setNewLongRentHourRateMultiplier(Double newLongRentHourRateMultiplier) {
        this.newLongRentHourRateMultiplier = newLongRentHourRateMultiplier;
    }

    public Double getOldExtraLongRentHourRateMultiplier() {
        return oldExtraLongRentHourRateMultiplier;
    }

    public void setOldExtraLongRentHourRateMultiplier(Double oldExtraLongRentHourRateMultiplier) {
        this.oldExtraLongRentHourRateMultiplier = oldExtraLongRentHourRateMultiplier;
    }

    public Double getNewExtraLongRentHourRateMultiplier() {
        return newExtraLongRentHourRateMultiplier;
    }

    public void setNewExtraLongRentHourRateMultiplier(Double newExtraLongRentHourRateMultiplier) {
        this.newExtraLongRentHourRateMultiplier = newExtraLongRentHourRateMultiplier;
    }

    public Long getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(Long operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
