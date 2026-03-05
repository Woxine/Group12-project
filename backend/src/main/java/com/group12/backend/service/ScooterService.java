package com.group12.backend.service;

import java.util.List;

import com.group12.backend.dto.UpdateScooterRequest;

public interface ScooterService {
    List<Object> getScooters(String status, Integer page, Integer limit);
    Object getScooterLocation(String scooterId);
    Object updateScooter(String scooterId, UpdateScooterRequest request);
}

