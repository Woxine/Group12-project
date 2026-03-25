package com.group12.backend.service;

import com.group12.backend.dto.CreateBookingRequest;

/**
 * 定义预约订单创建、取消和完成相关的服务能力。
 */
public interface BookingService {
    /**
     * 创建新的预约订单。
     */
    Object createBooking(CreateBookingRequest request);

    /**
     * 取消指定预约订单，并记录可选的结束位置。
     */
    Object cancelBooking(String bookingId, Double endLat, Double endLng);

    /**
     * 完成指定预约订单，并记录可选的结束位置。
     */
    Object completeBooking(String bookingId, Double endLat, Double endLng);
}
