package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.RevenueStatsDTO;
import com.group12.backend.dto.DurationRevenueDTO;
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

        // Filter by date range and exclude cancelled bookings from revenue
        List<Booking> filteredBookings = allBookings.stream()
                .filter(booking -> booking.getStartTime() != null)
                .filter(booking -> {
                    boolean startOk = startDate == null || !booking.getStartTime().toLocalDate().isBefore(startDate);
                    boolean endOk = endDate == null || !booking.getStartTime().toLocalDate().isAfter(endDate);
                    return startOk && endOk;
                })
                .filter(booking -> {
                    String status = booking.getStatus();
                    // Only count non-cancelled bookings
                    return status != null && !"CANCELLED".equalsIgnoreCase(status);
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

    @Override
    public List<DurationRevenueDTO> getWeeklyRevenueByDuration() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Booking> allBookings = bookingRepository.findAll();

        List<Booking> weekBookings = allBookings.stream()
                .filter(b -> b.getStartTime() != null)
                .filter(b -> {
                    LocalDate d = b.getStartTime().toLocalDate();
                    return (!d.isBefore(startOfWeek)) && (!d.isAfter(endOfWeek));
                })
                .filter(b -> {
                    String status = b.getStatus();
                    // Exclude cancelled bookings from weekly revenue
                    return status != null && !"CANCELLED".equalsIgnoreCase(status);
                })
                .collect(Collectors.toList());

        Map<String, List<Booking>> grouped = weekBookings.stream()
                .collect(Collectors.groupingBy(b -> toDurationType(b.getDurationHours())));

        return grouped.entrySet().stream()
                .map(e -> {
                    String durationType = e.getKey();
                    List<Booking> list = e.getValue();
                    BigDecimal totalRevenue = list.stream()
                            .map(Booking::getTotalPrice)
                            .filter(p -> p != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Integer totalOrders = list.size();
                    return new DurationRevenueDTO(durationType, totalRevenue, totalOrders);
                })
                .collect(Collectors.toList());
    }

    private static String toDurationType(Double hours) {
        if (hours == null) return "1H";
        if (hours > 0 && hours <= 10.0 / 60.0 + 0.01) return "10M";
        if (hours <= 1.0) return "1H";
        if (hours <= 4.0) {
            return "4H";
        }
        if (hours <= 24.0) {
            return "1D";
        }
        if (hours <= 168.0) {
            return "1W";
        }
        return hours.intValue() + "H";
    }
}
