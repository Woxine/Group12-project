package com.group12.backend.service;

import java.util.List;

import com.group12.backend.dto.RegisterRequest;

public interface UserService {
    Object register(RegisterRequest request);
    Object getUserBookings(String userId, Integer page, Integer size);
    Object getBookingById(String userId, String bookingId);
    Object getUserProfile(String userId);
}

