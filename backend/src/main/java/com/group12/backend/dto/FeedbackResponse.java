package com.group12.backend.dto;

/**
 * 用于返回用户故障反馈记录的内容、优先级和处理状态。
 */
public class FeedbackResponse {
    private Long id;
    private Long userId;
    private Long scooterId;
    private String content;
    private String priority;
    private Boolean resolved;
    private Boolean escalated;
    private String escalatedTo;
    private String escalationStatus;

    public FeedbackResponse(Long id, Long userId, Long scooterId, String content, String priority, Boolean resolved) {
        this(id, userId, scooterId, content, priority, resolved, false, null, null);
    }

    public FeedbackResponse(
            Long id,
            Long userId,
            Long scooterId,
            String content,
            String priority,
            Boolean resolved,
            Boolean escalated,
            String escalatedTo,
            String escalationStatus) {
        this.id = id;
        this.userId = userId;
        this.scooterId = scooterId;
        this.content = content;
        this.priority = priority;
        this.resolved = resolved;
        this.escalated = escalated;
        this.escalatedTo = escalatedTo;
        this.escalationStatus = escalationStatus;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getScooterId() { return scooterId; }
    public String getContent() { return content; }
    public String getPriority() { return priority; }
    public Boolean getResolved() { return resolved; }
    public Boolean getEscalated() { return escalated; }
    public String getEscalatedTo() { return escalatedTo; }
    public String getEscalationStatus() { return escalationStatus; }
}
