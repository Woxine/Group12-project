package com.group12.backend.service;

import com.group12.backend.dto.DurationRevenueDTO;
import com.group12.backend.dto.RevenueStatsDTO;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {
    RevenueStatsDTO getRevenueStatistics(LocalDate startDate, LocalDate endDate);
    List<DurationRevenueDTO> getWeeklyRevenueByDuration();
}
