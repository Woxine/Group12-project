package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用于接收管理员处理用户反馈时提交的状态和备注信息。
 */
public class UpdateFeedbackRequest {
    @NotBlank(message = "Status is required")
    private String status;
    private String note;
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

