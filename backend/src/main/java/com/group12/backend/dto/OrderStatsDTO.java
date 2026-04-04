package com.group12.backend.dto;

import java.math.BigDecimal;

/**
 * 用于展示订单总量、有效订单和取消率等指标。
 */
public class OrderStatsDTO {
    private Integer totalOrders;
    private Integer validOrders;
    private Integer cancelledOrders;
    private BigDecimal cancellationRate;

    public OrderStatsDTO(Integer totalOrders, Integer validOrders, Integer cancelledOrders, BigDecimal cancellationRate) {
        this.totalOrders = totalOrders;
        this.validOrders = validOrders;
        this.cancelledOrders = cancelledOrders;
        this.cancellationRate = cancellationRate;
    }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Integer getValidOrders() { return validOrders; }
    public void setValidOrders(Integer validOrders) { this.validOrders = validOrders; }

    public Integer getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(Integer cancelledOrders) { this.cancelledOrders = cancelledOrders; }

    public BigDecimal getCancellationRate() { return cancellationRate; }
    public void setCancellationRate(BigDecimal cancellationRate) { this.cancellationRate = cancellationRate; }
}
