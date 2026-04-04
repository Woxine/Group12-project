package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用于接收修改用户名请求中的新用户名（英文）。
 */
public class ChangeNameRequest {

    @NotBlank(message = "New username is required")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]*$", message = "Username must start with a letter and contain only letters, numbers, underscore")
    private String newName;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
