package com.group12.backend.dto;

import java.math.BigDecimal;

/**
 * 用于承载不同预约时长维度下的营收与订单统计结果。
 */
public class DurationRevenueDTO {

    private String durationType;
    private BigDecimal totalRevenue;
    private Integer totalOrders;

    public DurationRevenueDTO() {}

    public DurationRevenueDTO(String durationType, BigDecimal totalRevenue, Integer totalOrders) {
        this.durationType = durationType;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
    }

    public String getDurationType() { return durationType; }
    public void setDurationType(String durationType) { this.durationType = durationType; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
}
