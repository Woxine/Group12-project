package com.group12.backend.service.impl;

import org.springframework.stereotype.Service;

import com.group12.backend.dto.ExtendBookingRequest;
import com.group12.backend.service.BookingExtensionService;

/**
 * TODO(ID10&11): 预订延长实现骨架（仅 TODO，不含业务实现）。
 */
@Service
public class BookingExtensionServiceImpl implements BookingExtensionService {
    @Override
    public Object extendBooking(String bookingId, ExtendBookingRequest request, Long authUserId) {
        // TODO: 状态校验（仅 CONFIRMED 可延长）
        // TODO: 归属校验
        // TODO: 冲突检查 + 价格重算 + 事务更新
        throw new UnsupportedOperationException("TODO: implement extendBooking");
    }
}
