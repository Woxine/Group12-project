package com.group12.backend.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "billing_settings")
public class BillingSettings implements Serializable {
    @Id
    private Long id;

    @Column(name = "long_rent_threshold_hours", nullable = false)
    private BigDecimal longRentThresholdHours;

    @Column(name = "extra_long_rent_threshold_hours", nullable = false)
    private BigDecimal extraLongRentThresholdHours;

    @Column(name = "long_rent_multiplier", nullable = false)
    private BigDecimal longRentMultiplier;

    @Column(name = "extra_long_rent_multiplier", nullable = false)
    private BigDecimal extraLongRentMultiplier;

    @Column(name = "student_discount_rate", nullable = false)
    private BigDecimal studentDiscountRate;

    @Column(name = "senior_discount_rate", nullable = false)
    private BigDecimal seniorDiscountRate;

    @Column(name = "frequent_discount_rate", nullable = false)
    private BigDecimal frequentDiscountRate;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getLongRentMultiplier() {
        return longRentMultiplier;
    }

    public void setLongRentMultiplier(BigDecimal longRentMultiplier) {
        this.longRentMultiplier = longRentMultiplier;
    }

    public BigDecimal getExtraLongRentMultiplier() {
        return extraLongRentMultiplier;
    }

    public void setExtraLongRentMultiplier(BigDecimal extraLongRentMultiplier) {
        this.extraLongRentMultiplier = extraLongRentMultiplier;
    }

    public BigDecimal getStudentDiscountRate() {
        return studentDiscountRate;
    }

    public void setStudentDiscountRate(BigDecimal studentDiscountRate) {
        this.studentDiscountRate = studentDiscountRate;
    }

    public BigDecimal getSeniorDiscountRate() {
        return seniorDiscountRate;
    }

    public void setSeniorDiscountRate(BigDecimal seniorDiscountRate) {
        this.seniorDiscountRate = seniorDiscountRate;
    }

    public BigDecimal getFrequentDiscountRate() {
        return frequentDiscountRate;
    }

    public void setFrequentDiscountRate(BigDecimal frequentDiscountRate) {
        this.frequentDiscountRate = frequentDiscountRate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    public void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
