package com.group12.backend.service;

import com.group12.backend.dto.LoginRequest;

public interface AuthService {
    Object login(LoginRequest request);
    Object checkPermission();
}

