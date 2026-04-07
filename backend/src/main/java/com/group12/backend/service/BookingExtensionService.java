package com.group12.backend.service;

import com.group12.backend.dto.ExtendBookingRequest;

/**
 * TODO(ID10&11): 预订延长服务骨架。
 */
public interface BookingExtensionService {
    Object extendBooking(String bookingId, ExtendBookingRequest request, Long authUserId);
}
