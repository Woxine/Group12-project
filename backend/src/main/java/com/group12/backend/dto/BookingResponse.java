package com.group12.backend.dto;

import java.time.LocalDateTime;

public class BookingResponse {
    private String id;
    private String scooterId;
    private String userId;
    private String status;
    private LocalDateTime startTime;
    
    // Constructor, Getters, Setters
    public BookingResponse(String id, String scooterId, String userId, String status, LocalDateTime startTime) {
        this.id = id;
        this.scooterId = scooterId;
        this.userId = userId;
        this.status = status;
        this.startTime = startTime;
    }

    public String getId() { return id; }
    public String getScooterId() { return scooterId; }
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public LocalDateTime getStartTime() { return startTime; }
}

