package com.group12.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class UpdateProfileRequest {
    private Boolean isStudent;

    @Min(value = 0, message = "Age must be non-negative")
    @Max(value = 120, message = "Age must be reasonable")
    private Integer age;

    public Boolean getIsStudent() { return isStudent; }
    public void setIsStudent(Boolean isStudent) { this.isStudent = isStudent; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
