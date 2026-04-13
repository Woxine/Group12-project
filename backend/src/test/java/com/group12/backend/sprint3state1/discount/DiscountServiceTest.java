package com.group12.backend.sprint3state1.discount;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.service.impl.DiscountServiceImpl;

@DisplayName("ID22 DiscountService")
class DiscountServiceTest {

    private final DiscountServiceImpl discountService = new DiscountServiceImpl();

    @Test
    @DisplayName("calculateDiscount_studentApplied")
    void calculateDiscount_studentApplied() {
        DiscountBreakdownResponse response = discountService.calculateDiscount(1L, 1L, "1H");
        assertThat(response).isNotNull();
        assertThat(response.getDiscountType()).isEqualTo("STUDENT");
        assertThat(response.getFinalPrice()).isLessThan(response.getOriginalPrice());
    }

    @Test
    @DisplayName("calculateDiscount_priorityRuleStable")
    void calculateDiscount_priorityRuleStable() {
        String type = discountService.resolveDiscountType(999L);
        assertThat(type).isIn("FREQUENT", "STUDENT", "SENIOR");
    }

    @Test
    @DisplayName("applyDiscount_reducesPrice")
    void applyDiscount_reducesPrice() {
        BigDecimal finalPrice = discountService.applyDiscount(new BigDecimal("100.00"), "STUDENT");
        assertThat(finalPrice).isEqualByComparingTo("90.00");
    }
}
