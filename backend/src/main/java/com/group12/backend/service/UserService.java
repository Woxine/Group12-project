package com.group12.backend.service;

import java.util.List;

import com.group12.backend.dto.RegisterRequest;

public interface UserService {
    Object register(RegisterRequest request);
    List<Object> getUserBookings(String userId);
    Object getUserProfile(String userId);
}

