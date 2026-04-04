package com.group12.backend.dto;

import java.math.BigDecimal;

/**
 * 用于展示车辆总量、状态分布与快照使用率。
 */
public class VehicleStatsDTO {
    private Integer totalScooters;
    private Integer rentedScooters;
    private Integer maintenanceScooters;
    private Integer availableScooters;
    private BigDecimal usageRate;

    public VehicleStatsDTO(
            Integer totalScooters,
            Integer rentedScooters,
            Integer maintenanceScooters,
            Integer availableScooters,
            BigDecimal usageRate) {
        this.totalScooters = totalScooters;
        this.rentedScooters = rentedScooters;
        this.maintenanceScooters = maintenanceScooters;
        this.availableScooters = availableScooters;
        this.usageRate = usageRate;
    }

    public Integer getTotalScooters() { return totalScooters; }
    public void setTotalScooters(Integer totalScooters) { this.totalScooters = totalScooters; }

    public Integer getRentedScooters() { return rentedScooters; }
    public void setRentedScooters(Integer rentedScooters) { this.rentedScooters = rentedScooters; }

    public Integer getMaintenanceScooters() { return maintenanceScooters; }
    public void setMaintenanceScooters(Integer maintenanceScooters) { this.maintenanceScooters = maintenanceScooters; }

    public Integer getAvailableScooters() { return availableScooters; }
    public void setAvailableScooters(Integer availableScooters) { this.availableScooters = availableScooters; }

    public BigDecimal getUsageRate() { return usageRate; }
    public void setUsageRate(BigDecimal usageRate) { this.usageRate = usageRate; }
}
