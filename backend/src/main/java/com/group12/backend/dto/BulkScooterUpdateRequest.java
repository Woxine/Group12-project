package com.group12.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload for preview/apply operations that update all scooters of one type.
 */
public class BulkScooterUpdateRequest {

    @NotBlank(message = "type is required")
    private String type;

    private BigDecimal hour_rate;
    private String status;
    private Boolean visible;
    private Boolean confirm_risky;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getHour_rate() {
        return hour_rate;
    }

    public void setHour_rate(BigDecimal hour_rate) {
        this.hour_rate = hour_rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getConfirm_risky() {
        return confirm_risky;
    }

    public void setConfirm_risky(Boolean confirm_risky) {
        this.confirm_risky = confirm_risky;
    }
}
