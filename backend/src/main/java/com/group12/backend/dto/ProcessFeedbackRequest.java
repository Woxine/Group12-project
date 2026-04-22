package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;

/**
 * ID14 TODO: 请求管理员按优先级处理反馈（直接处理或上报）。
 */
public class ProcessFeedbackRequest {
    @NotBlank(message = "Action is required")
    @Pattern(regexp = "DIRECT_HANDLE|ESCALATE", message = "Action must be DIRECT_HANDLE or ESCALATE")
    private String action;

    private String escalateTo;

    private String note;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEscalateTo() {
        return escalateTo;
    }

    public void setEscalateTo(String escalateTo) {
        this.escalateTo = escalateTo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @AssertTrue(message = "Escalate target is required when action is ESCALATE")
    public boolean isEscalateToValidForAction() {
        if (!"ESCALATE".equals(action)) {
            return true;
        }
        return escalateTo != null && !escalateTo.trim().isEmpty();
    }
}
