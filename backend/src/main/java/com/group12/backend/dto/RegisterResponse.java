package com.group12.backend.dto;

/**
 * 用于返回用户注册成功后的账户标识和基础资料。
 */
public class RegisterResponse {
    private String userId;
    private String email;
    private String name;

    public RegisterResponse(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}

