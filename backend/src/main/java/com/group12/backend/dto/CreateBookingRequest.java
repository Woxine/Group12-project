package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBookingRequest {
    @NotBlank(message = "Scooter ID is required")
    private String scooter_id;

    @NotBlank(message = "User ID is required")
    private String user_id;

    @NotBlank(message = "Duration is required")
    @jakarta.validation.constraints.Pattern(regexp = "^(1H|4H|1D|1W)$", message = "Duration must be one of: 1H, 4H, 1D, 1W")
    private String duration; // 1H, 4H, 1D, 1W

    public String getScooter_id() { return scooter_id; }
    public void setScooter_id(String scooter_id) { this.scooter_id = scooter_id; }
    
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}

