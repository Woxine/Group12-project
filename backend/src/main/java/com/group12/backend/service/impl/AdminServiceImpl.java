package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.RevenueStatsDTO;
import com.group12.backend.entity.Booking;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public RevenueStatsDTO getRevenueStatistics(LocalDate startDate, LocalDate endDate) {
        List<Booking> allBookings = bookingRepository.findAll();

        // Filter by date range if provided
        List<Booking> filteredBookings = allBookings.stream()
                .filter(booking -> {
                    boolean startOk = startDate == null || !booking.getStartTime().toLocalDate().isBefore(startDate);
                    boolean endOk = endDate == null || !booking.getStartTime().toLocalDate().isAfter(endDate);
                    return startOk && endOk;
                })
                .collect(Collectors.toList());

        BigDecimal totalRevenue = filteredBookings.stream()
                .map(Booking::getTotalPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalOrders = filteredBookings.size();

        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
        }

        return new RevenueStatsDTO(totalRevenue, totalOrders, averageOrderValue);
    }
}
