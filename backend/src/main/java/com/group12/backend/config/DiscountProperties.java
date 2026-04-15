package com.group12.backend.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "discount")
public class DiscountProperties {
    private boolean enabled = true;
    private BigDecimal rate = new BigDecimal("0.8");
    private int elderlyMinAge = 60;
    private int rollingDays = 7;
    private double heavyUserHoursThreshold = 8.0;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public int getElderlyMinAge() { return elderlyMinAge; }
    public void setElderlyMinAge(int elderlyMinAge) { this.elderlyMinAge = elderlyMinAge; }

    public int getRollingDays() { return rollingDays; }
    public void setRollingDays(int rollingDays) { this.rollingDays = rollingDays; }

    public double getHeavyUserHoursThreshold() { return heavyUserHoursThreshold; }
    public void setHeavyUserHoursThreshold(double heavyUserHoursThreshold) { this.heavyUserHoursThreshold = heavyUserHoursThreshold; }
}
