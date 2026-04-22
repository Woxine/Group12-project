package com.group12.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group12.backend.dto.DailyTrendPointDTO;
import com.group12.backend.dto.DashboardOverviewDTO;
import com.group12.backend.dto.DurationRevenueDTO;
import com.group12.backend.dto.FaultStatsDTO;
import com.group12.backend.dto.OrderStatsDTO;
import com.group12.backend.dto.PopularRentalDateDTO;
import com.group12.backend.dto.RevenueStatsDTO;
import com.group12.backend.dto.VehicleStatsDTO;
import com.group12.backend.entity.Booking;
import com.group12.backend.entity.Feedback;
import com.group12.backend.entity.Scooter;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.repository.FeedbackRepository;
import com.group12.backend.repository.ScooterRepository;
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

    @Autowired
    private ScooterRepository scooterRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

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

    @Override
    public DashboardOverviewDTO getDashboardOverview(LocalDate startDate, LocalDate endDate) {
        List<Booking> allBookingsInRange = getBookingsWithinRange(startDate, endDate);
        List<Booking> validBookings = getValidBookings(allBookingsInRange);

        return new DashboardOverviewDTO(
                buildOrderStats(allBookingsInRange, validBookings),
                buildRevenueStats(validBookings),
                buildVehicleStats(),
                buildFaultStats(),
                buildDailyTrend(validBookings, startDate, endDate));
    }

    @Override
    public List<PopularRentalDateDTO> getPopularRentalDatesThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Booking> validBookingsThisWeek = getValidBookingsWithinRange(startOfWeek, endOfWeek);
        List<DailyTrendPointDTO> weeklyTrend = buildDailyTrend(validBookingsThisWeek, startOfWeek, endOfWeek);

        Comparator<DailyTrendPointDTO> leaderboardComparator = Comparator
                .comparing(
                        (DailyTrendPointDTO point) -> point.getRevenue() == null ? BigDecimal.ZERO : point.getRevenue(),
                        Comparator.reverseOrder())
                .thenComparing(DailyTrendPointDTO::getOrderCount, Comparator.reverseOrder())
                .thenComparing(DailyTrendPointDTO::getDate, Comparator.reverseOrder());

        List<DailyTrendPointDTO> sortedTrend = weeklyTrend.stream()
                .sorted(leaderboardComparator)
                .collect(Collectors.toList());

        int rank = 1;
        List<PopularRentalDateDTO> leaderboard = new java.util.ArrayList<>();
        for (DailyTrendPointDTO point : sortedTrend) {
            PopularRentalDateDTO item = new PopularRentalDateDTO();
            item.setDate(point.getDate());
            item.setRank(rank++);
            item.setOrderCount(point.getOrderCount());
            item.setRevenue(point.getRevenue() == null ? BigDecimal.ZERO : point.getRevenue());
            leaderboard.add(item);
        }
        return leaderboard;
    }

    private OrderStatsDTO buildOrderStats(List<Booking> allBookingsInRange, List<Booking> validBookings) {
        int totalOrders = allBookingsInRange.size();
        int validOrders = validBookings.size();
        int cancelledOrders = totalOrders - validOrders;
        BigDecimal cancellationRate = BigDecimal.ZERO;
        if (totalOrders > 0) {
            cancellationRate = BigDecimal.valueOf(cancelledOrders)
                    .divide(BigDecimal.valueOf(totalOrders), 4, RoundingMode.HALF_UP);
        }
        return new OrderStatsDTO(totalOrders, validOrders, cancelledOrders, cancellationRate);
    }

    private RevenueStatsDTO buildRevenueStats(List<Booking> validBookings) {
        BigDecimal totalRevenue = sumRevenue(validBookings);
        int totalOrders = validBookings.size();
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
        }
        return new RevenueStatsDTO(totalRevenue, totalOrders, averageOrderValue);
    }

    private VehicleStatsDTO buildVehicleStats() {
        List<Scooter> scooters = scooterRepository.findAll();
        int totalScooters = scooters.size();
        int rentedScooters = countScootersByStatus(scooters, "RENTED");
        int maintenanceScooters = countScootersByStatus(scooters, "MAINTENANCE");
        int availableScooters = countScootersByStatus(scooters, "AVAILABLE");
        BigDecimal usageRate = BigDecimal.ZERO;
        if (totalScooters > 0) {
            usageRate = BigDecimal.valueOf(rentedScooters)
                    .divide(BigDecimal.valueOf(totalScooters), 4, RoundingMode.HALF_UP);
        }
        return new VehicleStatsDTO(totalScooters, rentedScooters, maintenanceScooters, availableScooters, usageRate);
    }

    private FaultStatsDTO buildFaultStats() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        int totalFeedbacks = feedbacks.size();
        int resolvedFeedbacks = (int) feedbacks.stream().filter(f -> Boolean.TRUE.equals(f.getResolved())).count();
        int unresolvedFeedbacks = totalFeedbacks - resolvedFeedbacks;
        Map<String, Integer> priorityDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        f -> normalizePriority(f.getPriority()),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        return new FaultStatsDTO(totalFeedbacks, resolvedFeedbacks, unresolvedFeedbacks, priorityDistribution);
    }

    private List<DailyTrendPointDTO> buildDailyTrend(List<Booking> validBookings, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, List<Booking>> groupedByDate = validBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getStartTime().toLocalDate()));

        Stream<LocalDate> dateStream;
        if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
            dateStream = startDate.datesUntil(endDate.plusDays(1));
        } else {
            dateStream = groupedByDate.keySet().stream().sorted(Comparator.naturalOrder());
        }

        return dateStream
                .map(date -> {
                    List<Booking> dayBookings = groupedByDate.getOrDefault(date, List.of());
                    return new DailyTrendPointDTO(date, dayBookings.size(), sumRevenue(dayBookings));
                })
                .collect(Collectors.toList());
    }

    private int countScootersByStatus(List<Scooter> scooters, String status) {
        return (int) scooters.stream()
                .filter(scooter -> scooter.getStatus() != null && status.equalsIgnoreCase(scooter.getStatus()))
                .count();
    }

    private String normalizePriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return "UNKNOWN";
        }
        return priority.trim().toUpperCase();
    }

    private List<Booking> getValidBookingsWithinRange(LocalDate startDate, LocalDate endDate) {
        return getValidBookings(getBookingsWithinRange(startDate, endDate));
    }

    private List<Booking> getBookingsWithinRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getStartTime() != null)
                .filter(booking -> {
                    LocalDate bookingDate = booking.getStartTime().toLocalDate();
                    boolean startOk = startDate == null || !bookingDate.isBefore(startDate);
                    boolean endOk = endDate == null || !bookingDate.isAfter(endDate);
                    return startOk && endOk;
                })
                .collect(Collectors.toList());
    }

    private List<Booking> getValidBookings(List<Booking> bookings) {
        return bookings.stream()
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
        if (hours == null || hours <= 0) return "Unknown";
        final double eps = 0.01;
        if (Math.abs(hours - TEN_MINUTES_IN_HOURS) < eps) return "10M";
        if (Math.abs(hours - 1.0) < eps) return "1H";
        if (Math.abs(hours - 4.0) < eps) return "4H";
        if (Math.abs(hours - 24.0) < eps) return "1D";
        if (Math.abs(hours - 168.0) < eps) return "1W";

        long totalMinutes = Math.round(hours * 60.0);
        if (totalMinutes < 60) {
            return totalMinutes + "M";
        }

        if (totalMinutes % 60 == 0) {
            return (totalMinutes / 60) + "H";
        }

        return String.format("%.2fH", hours);
    }

    private static int durationOrderIndex(String durationType) {
        int index = DURATION_ORDER.indexOf(durationType);
        return index >= 0 ? index : DURATION_ORDER.size();
    }
}
