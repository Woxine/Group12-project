package com.group12.backend.dto;

public class LoginResponse {
    private String token;
    private String userId;
    private String role;
    
    public LoginResponse(String token, String userId, String role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }
    // Getters
    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
}

