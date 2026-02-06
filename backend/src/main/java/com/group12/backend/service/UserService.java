package com.group12.backend.service;

import com.group12.backend.dto.RegisterRequest;
import java.util.List;

public interface UserService {
    Object register(RegisterRequest request);
    List<Object> getUserBookings(String userId);
    Object getUserProfile(String userId);
}

