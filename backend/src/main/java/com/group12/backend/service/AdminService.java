package com.group12.backend.service;

import com.group12.backend.dto.DurationRevenueDTO;
import com.group12.backend.dto.DashboardOverviewDTO;
import com.group12.backend.dto.RevenueStatsDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 定义后台管理端营收统计与经营分析相关的服务能力。
 */
public interface AdminService {
    /**
     * 按日期范围统计总收入、订单数和客单价。
     */
    RevenueStatsDTO getRevenueStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 按日期范围统计租期分类的营收和订单数量。
     */
    List<DurationRevenueDTO> getRevenueByDuration(LocalDate startDate, LocalDate endDate);

    /**
     * 统计本周按租期分类的营收和订单数量。
     */
    List<DurationRevenueDTO> getWeeklyRevenueByDuration();

    /**
     * 按日期范围聚合管理看板核心指标数据。
     */
    DashboardOverviewDTO getDashboardOverview(LocalDate startDate, LocalDate endDate);
}
