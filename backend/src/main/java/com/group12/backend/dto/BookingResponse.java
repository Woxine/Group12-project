package com.group12.backend.dto;

import java.time.LocalDateTime;

public class BookingResponse {
    private String id;
    private String scooterId;
    private String userId;
    private String status;
    private LocalDateTime createdAt;
    
    // Constructor, Getters, Setters
    public BookingResponse(String id, String scooterId, String userId, String status, LocalDateTime createdAt) {
        this.id = id;
        this.scooterId = scooterId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }
}

