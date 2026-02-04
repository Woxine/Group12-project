package com.group12.backend.service;

import com.group12.backend.dto.RevenueStatsDTO;
import java.time.LocalDate;

public interface AdminService {
    RevenueStatsDTO getRevenueStatistics(LocalDate startDate, LocalDate endDate);
}

