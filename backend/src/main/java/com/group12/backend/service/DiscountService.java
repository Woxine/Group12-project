package com.group12.backend.service;

import com.group12.backend.dto.DiscountBreakdownResponse;

/**
 * TODO(ID22): 折扣能力接口骨架。
 */
public interface DiscountService {

    DiscountBreakdownResponse calculateDiscount(Long userId, Long scooterId, String duration);

    String resolveDiscountType(Long userId);

    boolean hasFrequentDiscount(Long userId);

    boolean hasStudentDiscount(Long userId);

    boolean hasSeniorDiscount(Long userId);
}
