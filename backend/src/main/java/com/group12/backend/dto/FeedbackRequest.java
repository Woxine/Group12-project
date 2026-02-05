package com.group12.backend.dto;

public class FeedbackRequest {
    private String scooter_id;
    private String description;
    private String location;

    // Getters Setters
    public String getScooter_id() { return scooter_id; }
    public void setScooter_id(String scooter_id) { this.scooter_id = scooter_id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

