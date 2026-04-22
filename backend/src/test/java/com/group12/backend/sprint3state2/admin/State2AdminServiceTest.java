package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group12.backend.dto.PopularRentalDateDTO;
import com.group12.backend.entity.Booking;
import com.group12.backend.repository.BookingRepository;
import com.group12.backend.service.impl.AdminServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sprint3-State2 AdminService")
class State2AdminServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @DisplayName("getPopularRentalDatesThisWeek_returns7DaysSortedAndRanked")
    void getPopularRentalDatesThisWeek_returns7DaysSortedAndRanked() {
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);

        Booking tuesdayHighOrdersA = booking(startOfWeek.plusDays(1), "COMPLETED", "60.00");
        Booking tuesdayHighOrdersB = booking(startOfWeek.plusDays(1), "CONFIRMED", "40.00");
        Booking fridayTieLaterDate = booking(startOfWeek.plusDays(4), "CONFIRMED", "100.00");
        Booking mondayTieEarlierDate = booking(startOfWeek, "COMPLETED", "100.00");
        Booking wednesdayLowerRevenueA = booking(startOfWeek.plusDays(2), "COMPLETED", "30.00");
        Booking wednesdayLowerRevenueB = booking(startOfWeek.plusDays(2), "COMPLETED", "30.00");
        Booking wednesdayLowerRevenueC = booking(startOfWeek.plusDays(2), "COMPLETED", "30.00");

        Booking cancelledShouldBeIgnored = booking(startOfWeek.plusDays(3), "CANCELLED", "500.00");
        Booking nextWeekShouldBeIgnored = booking(startOfWeek.plusDays(7), "COMPLETED", "200.00");

        when(bookingRepository.findAll()).thenReturn(List.of(
                tuesdayHighOrdersA, tuesdayHighOrdersB,
                fridayTieLaterDate, mondayTieEarlierDate,
                wednesdayLowerRevenueA, wednesdayLowerRevenueB, wednesdayLowerRevenueC,
                cancelledShouldBeIgnored, nextWeekShouldBeIgnored));

        List<PopularRentalDateDTO> result = adminService.getPopularRentalDatesThisWeek();

        assertThat(result).hasSize(7);
        assertThat(result).extracting(PopularRentalDateDTO::getRank)
                .containsExactly(1, 2, 3, 4, 5, 6, 7);

        assertThat(result.get(0).getDate()).isEqualTo(startOfWeek.plusDays(1));
        assertThat(result.get(0).getRevenue()).isEqualByComparingTo("100.00");
        assertThat(result.get(0).getOrderCount()).isEqualTo(2);

        assertThat(result.get(1).getDate()).isEqualTo(startOfWeek.plusDays(4));
        assertThat(result.get(1).getRevenue()).isEqualByComparingTo("100.00");
        assertThat(result.get(1).getOrderCount()).isEqualTo(1);

        assertThat(result.get(2).getDate()).isEqualTo(startOfWeek);
        assertThat(result.get(2).getRevenue()).isEqualByComparingTo("100.00");
        assertThat(result.get(2).getOrderCount()).isEqualTo(1);

        assertThat(result.get(3).getDate()).isEqualTo(startOfWeek.plusDays(2));
        assertThat(result.get(3).getRevenue()).isEqualByComparingTo("90.00");
        assertThat(result.get(3).getOrderCount()).isEqualTo(3);

        Map<LocalDate, PopularRentalDateDTO> byDate = result.stream()
                .collect(Collectors.toMap(PopularRentalDateDTO::getDate, item -> item));

        assertThat(byDate.get(startOfWeek.plusDays(6)).getRevenue()).isEqualByComparingTo("0");
        assertThat(byDate.get(startOfWeek.plusDays(6)).getOrderCount()).isZero();
        assertThat(byDate.get(startOfWeek.plusDays(5)).getRevenue()).isEqualByComparingTo("0");
        assertThat(byDate.get(startOfWeek.plusDays(5)).getOrderCount()).isZero();
        assertThat(byDate.get(startOfWeek.plusDays(3)).getRevenue()).isEqualByComparingTo("0");
        assertThat(byDate.get(startOfWeek.plusDays(3)).getOrderCount()).isZero();
    }

    private static Booking booking(LocalDate date, String status, String totalPrice) {
        Booking booking = new Booking();
        booking.setStartTime(LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 10, 0));
        booking.setStatus(status);
        booking.setTotalPrice(new BigDecimal(totalPrice));
        return booking;
    }
}
