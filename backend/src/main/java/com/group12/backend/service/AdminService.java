package com.group12.backend.service;

import com.group12.backend.dto.DurationRevenueDTO;
import com.group12.backend.dto.DashboardOverviewDTO;
import com.group12.backend.dto.PopularRentalDateDTO;
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

    /**
     * ID20 TODO: 获取本周热门租赁日期周榜（按日收入/订单数排序）。
     */
    List<PopularRentalDateDTO> getPopularRentalDatesThisWeek();

    /**
     * 获取指定日期范围内热门租赁日期榜单（按日收入/订单数排序）。
     */
    List<PopularRentalDateDTO> getPopularRentalDates(LocalDate startDate, LocalDate endDate);
}
