package com.group12.backend.service;

import com.group12.backend.dto.RegisterRequest;

/**
 * 定义用户注册、资料查询和预约历史查询相关的服务能力。
 */
public interface UserService {
    /**
     * 注册新用户账户。
     */
    Object register(RegisterRequest request);

    /**
     * 查询指定用户的预约历史记录。
     */
    Object getUserBookings(String userId, Integer page, Integer size);

    /**
     * 查询指定用户的单条预约详情。
     */
    Object getBookingById(String userId, String bookingId);

    /**
     * 获取指定用户的个人资料。
     */
    Object getUserProfile(String userId);
}
