package com.group12.backend.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.service.DiscountService;

/**
 * TODO(ID22): 折扣规则实现骨架（仅保留方法框架，不实现业务）。
 */
@Service
public class DiscountServiceImpl implements DiscountService {

    @Override
    public DiscountBreakdownResponse calculateDiscount(Long userId, Long scooterId, String duration) {
        // TODO(ID22): orchestrate eligibility check and price calculation.
        return DiscountBreakdownResponse.of(BigDecimal.ZERO, "NONE", BigDecimal.ZERO, BigDecimal.ZERO, "discount.none");
    }

    @Override
    public String resolveDiscountType(Long userId) {
        // TODO(ID22): apply discount priority strategy.
        return "NONE";
    }

    public boolean isFrequentUser(Long userId) {
        // TODO(ID22): aggregate last 7 days booking duration and compare with 8h threshold.
        return false;
    }

    public boolean isStudent(Long userId) {
        // TODO(ID22): read user.isStudent safely.
        return false;
    }

    public boolean isSenior(Long userId) {
        // TODO(ID22): evaluate age threshold rule.
        return false;
    }

    public BigDecimal applyDiscount(BigDecimal origin, String discountType) {
        // TODO(ID22): calculate discounted price with scale/rounding policy.
        return origin;
    }
}
