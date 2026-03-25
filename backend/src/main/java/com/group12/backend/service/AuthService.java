package com.group12.backend.service;

import com.group12.backend.dto.LoginRequest;

/**
 * 定义用户认证、登录和权限校验相关的服务能力。
 */
public interface AuthService {
    /**
     * 校验登录凭据并返回认证结果。
     */
    Object login(LoginRequest request);

    /**
     * 检查当前用户的登录状态或权限是否合法。
     */
    Object checkPermission();
}

