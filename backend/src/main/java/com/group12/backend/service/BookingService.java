package com.group12.backend.service;

import com.group12.backend.dto.CreateBookingRequest;
import com.group12.backend.dto.CreateGuestBookingRequest;
import com.group12.backend.dto.DiscountBreakdownResponse;
import com.group12.backend.dto.PayBookingRequest;

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

    /**
     * 为待支付订单完成支付并正式启用车辆。
     */
    default Object payBooking(String bookingId, Long authUserId, PayBookingRequest request) {
        throw new UnsupportedOperationException("payBooking not implemented yet");
    }

    /**
     * TODO(ID9): 店员为未注册用户创建预约订单。
     */
    default Object createGuestBooking(CreateGuestBookingRequest request) {
        throw new UnsupportedOperationException("TODO(ID9): createGuestBooking not implemented yet");
    }

    /**
     * TODO(ID22): 订单创建前执行折扣试算。
     */
    default DiscountBreakdownResponse previewDiscount(String userId, String scooterId, String duration) {
        throw new UnsupportedOperationException("TODO(ID22): previewDiscount not implemented yet");
    }
}
