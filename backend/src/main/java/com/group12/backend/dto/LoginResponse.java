package com.group12.backend.dto;

public class LoginResponse {
    private String token;
    private String userId;
    private String role;
    private String name;
    
    public LoginResponse(String token, String userId, String role, String name) {
        this.token = token;
        this.userId = userId;
        this.role = role;
        this.name = name;
    }
    // Getters
    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getName() { return name; }
}

