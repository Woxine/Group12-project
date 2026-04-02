package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.DurationRevenueDTO;
import com.group12.backend.dto.RevenueStatsDTO;
import com.group12.backend.entity.Booking;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.service.AdminService;

/**
 * 实现后台收入统计与按租期经营分析相关的业务逻辑。
 */
@Service
public class AdminServiceImpl implements AdminService {
    private static final double TEN_MINUTES_IN_HOURS = 10.0 / 60.0;
    private static final List<String> DURATION_ORDER = List.of("10M", "1H", "4H", "1D", "1W");

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    /**
     * 根据日期范围汇总有效订单的营收概览数据。
     */
    public RevenueStatsDTO getRevenueStatistics(LocalDate startDate, LocalDate endDate) {
        List<Booking> filteredBookings = getValidBookingsWithinRange(startDate, endDate);

        BigDecimal totalRevenue = sumRevenue(filteredBookings);

        Integer totalOrders = filteredBookings.size();

        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
        }

        return new RevenueStatsDTO(totalRevenue, totalOrders, averageOrderValue);
    }

    @Override
    /**
     * 统计指定日期范围内按租期分类的收入和订单数量。
     */
    public List<DurationRevenueDTO> getRevenueByDuration(LocalDate startDate, LocalDate endDate) {
        return buildDurationRevenue(getValidBookingsWithinRange(startDate, endDate));
    }

    @Override
    /**
     * 统计当前自然周内按租期分类的收入和订单数量。
     */
    public List<DurationRevenueDTO> getWeeklyRevenueByDuration() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return getRevenueByDuration(startOfWeek, endOfWeek);
    }

    private List<Booking> getValidBookingsWithinRange(LocalDate startDate, LocalDate endDate) {
        List<Booking> allBookings = bookingRepository.findAll();
        return allBookings.stream()
                .filter(booking -> booking.getStartTime() != null)
                .filter(booking -> {
                    LocalDate bookingDate = booking.getStartTime().toLocalDate();
                    boolean startOk = startDate == null || !bookingDate.isBefore(startDate);
                    boolean endOk = endDate == null || !bookingDate.isAfter(endDate);
                    return startOk && endOk;
                })
                .filter(booking -> {
                    String status = booking.getStatus();
                    return status != null && !"CANCELLED".equalsIgnoreCase(status);
                })
                .collect(Collectors.toList());
    }

    private List<DurationRevenueDTO> buildDurationRevenue(List<Booking> bookings) {
        Map<String, List<Booking>> grouped = bookings.stream()
                .collect(Collectors.groupingBy(b -> toDurationType(b.getDurationHours())));

        return grouped.entrySet().stream()
                .sorted((left, right) -> Integer.compare(
                        durationOrderIndex(left.getKey()),
                        durationOrderIndex(right.getKey())))
                .map(e -> {
                    BigDecimal totalRevenue = sumRevenue(e.getValue());
                    return new DurationRevenueDTO(e.getKey(), totalRevenue, e.getValue().size());
                })
                .collect(Collectors.toList());
    }

    private BigDecimal sumRevenue(List<Booking> bookings) {
        return bookings.stream()
                .map(Booking::getTotalPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 将预约时长换算为统计展示使用的租期分类标签。
     */
    private static String toDurationType(Double hours) {
        if (hours == null) return "Unknown";
        if (Math.abs(hours - TEN_MINUTES_IN_HOURS) < 0.001) return "10M";
        if (Math.abs(hours - 1.0) < 0.001) return "1H";
        if (Math.abs(hours - 4.0) < 0.001) return "4H";
        if (Math.abs(hours - 24.0) < 0.001) return "1D";
        if (Math.abs(hours - 168.0) < 0.001) return "1W";
        return hours.intValue() + "H";
    }

    private static int durationOrderIndex(String durationType) {
        int index = DURATION_ORDER.indexOf(durationType);
        return index >= 0 ? index : DURATION_ORDER.size();
    }
}
