package com.group12.backend.dto;

import java.util.List;

/**
 * 管理端看板总览数据传输对象。
 */
public class DashboardOverviewDTO {
    private OrderStatsDTO orderStats;
    private RevenueStatsDTO revenueStats;
    private VehicleStatsDTO vehicleStats;
    private FaultStatsDTO faultStats;
    private List<DailyTrendPointDTO> dailyTrend;

    public DashboardOverviewDTO(
            OrderStatsDTO orderStats,
            RevenueStatsDTO revenueStats,
            VehicleStatsDTO vehicleStats,
            FaultStatsDTO faultStats,
            List<DailyTrendPointDTO> dailyTrend) {
        this.orderStats = orderStats;
        this.revenueStats = revenueStats;
        this.vehicleStats = vehicleStats;
        this.faultStats = faultStats;
        this.dailyTrend = dailyTrend;
    }

    public OrderStatsDTO getOrderStats() { return orderStats; }
    public void setOrderStats(OrderStatsDTO orderStats) { this.orderStats = orderStats; }

    public RevenueStatsDTO getRevenueStats() { return revenueStats; }
    public void setRevenueStats(RevenueStatsDTO revenueStats) { this.revenueStats = revenueStats; }

    public VehicleStatsDTO getVehicleStats() { return vehicleStats; }
    public void setVehicleStats(VehicleStatsDTO vehicleStats) { this.vehicleStats = vehicleStats; }

    public FaultStatsDTO getFaultStats() { return faultStats; }
    public void setFaultStats(FaultStatsDTO faultStats) { this.faultStats = faultStats; }

    public List<DailyTrendPointDTO> getDailyTrend() { return dailyTrend; }
    public void setDailyTrend(List<DailyTrendPointDTO> dailyTrend) { this.dailyTrend = dailyTrend; }
}
