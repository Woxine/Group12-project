package com.group12.backend.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "billing_settings_logs")
public class BillingSettingsLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "old_long_rent_multiplier", nullable = false)
    private BigDecimal oldLongRentMultiplier;

    @Column(name = "new_long_rent_multiplier", nullable = false)
    private BigDecimal newLongRentMultiplier;

    @Column(name = "old_extra_long_rent_multiplier", nullable = false)
    private BigDecimal oldExtraLongRentMultiplier;

    @Column(name = "new_extra_long_rent_multiplier", nullable = false)
    private BigDecimal newExtraLongRentMultiplier;

    @Column(name = "operator_user_id")
    private Long operatorUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOldLongRentMultiplier() {
        return oldLongRentMultiplier;
    }

    public void setOldLongRentMultiplier(BigDecimal oldLongRentMultiplier) {
        this.oldLongRentMultiplier = oldLongRentMultiplier;
    }

    public BigDecimal getNewLongRentMultiplier() {
        return newLongRentMultiplier;
    }

    public void setNewLongRentMultiplier(BigDecimal newLongRentMultiplier) {
        this.newLongRentMultiplier = newLongRentMultiplier;
    }

    public BigDecimal getOldExtraLongRentMultiplier() {
        return oldExtraLongRentMultiplier;
    }

    public void setOldExtraLongRentMultiplier(BigDecimal oldExtraLongRentMultiplier) {
        this.oldExtraLongRentMultiplier = oldExtraLongRentMultiplier;
    }

    public BigDecimal getNewExtraLongRentMultiplier() {
        return newExtraLongRentMultiplier;
    }

    public void setNewExtraLongRentMultiplier(BigDecimal newExtraLongRentMultiplier) {
        this.newExtraLongRentMultiplier = newExtraLongRentMultiplier;
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

    @PrePersist
    public void initCreatedAt() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
