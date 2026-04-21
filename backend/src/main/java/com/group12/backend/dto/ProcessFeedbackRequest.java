package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ID14 TODO: 请求管理员按优先级处理反馈（直接处理或上报）。
 */
public class ProcessFeedbackRequest {
    @NotBlank(message = "Action is required")
    private String action; // TODO: allow "DIRECT_HANDLE" | "ESCALATE"

    private String escalateTo; // TODO: target team/role when action is ESCALATE

    private String note; // TODO: operation note

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
}
