package com.group12.backend.sprint3state2.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.PopularRentalDateDTO;

@DisplayName("ID20 PopularRentalDateDTO")
class PopularRentalDateDTOTest {

    @Test
    @DisplayName("getterSetter_roundTrip")
    void getterSetter_roundTrip() {
        PopularRentalDateDTO dto = new PopularRentalDateDTO();
        dto.setDate(LocalDate.of(2026, 4, 21));
        dto.setRank(1);
        dto.setOrderCount(8);
        dto.setRevenue(new BigDecimal("180.50"));

        assertThat(dto.getDate()).isEqualTo(LocalDate.of(2026, 4, 21));
        assertThat(dto.getRank()).isEqualTo(1);
        assertThat(dto.getOrderCount()).isEqualTo(8);
        assertThat(dto.getRevenue()).isEqualByComparingTo("180.50");
    }
}
