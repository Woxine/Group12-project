package com.group12.backend.service;

import java.util.List;

public interface ScooterService {
    List<Object> getScooters(String status, Integer page, Integer limit);
    Object getScooterLocation(String scooterId);
}

