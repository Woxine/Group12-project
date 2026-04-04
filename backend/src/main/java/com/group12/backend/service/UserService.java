package com.group12.backend.service;

import com.group12.backend.dto.RegisterRequest;
import com.group12.backend.dto.ChangePasswordRequest;
import com.group12.backend.dto.ChangeEmailRequest;
import com.group12.backend.dto.ChangeNameRequest;

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

    /**
     * 修改指定用户密码（需校验原密码）。
     */
    Object changePassword(String userId, ChangePasswordRequest request);

    /**
     * 修改指定用户邮箱（需校验邮箱唯一性）。
     */
    Object changeEmail(String userId, ChangeEmailRequest request);

    /**
     * 修改指定用户名（需校验格式与唯一性）。
     */
    Object changeName(String userId, ChangeNameRequest request);
}
