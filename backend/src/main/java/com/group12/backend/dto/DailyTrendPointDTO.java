package com.group12.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用于展示管理端按日聚合的订单与收入走势点。
 */
public class DailyTrendPointDTO {
    private LocalDate date;
    private Integer orderCount;
    private BigDecimal revenue;

    public DailyTrendPointDTO(LocalDate date, Integer orderCount, BigDecimal revenue) {
        this.date = date;
        this.orderCount = orderCount;
        this.revenue = revenue;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
