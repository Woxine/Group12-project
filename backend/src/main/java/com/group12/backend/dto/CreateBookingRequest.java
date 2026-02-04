package com.group12.backend.dto;

public class CreateBookingRequest {
    private String scooter_id;
    private String user_id;

    public String getScooter_id() { return scooter_id; }
    public void setScooter_id(String scooter_id) { this.scooter_id = scooter_id; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }
}

