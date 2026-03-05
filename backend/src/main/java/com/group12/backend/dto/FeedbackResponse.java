package com.group12.backend.dto;

public class FeedbackResponse {
    private Long id;
    private Long userId;
    private Long scooterId;
    private String content;
    private String priority;
    private Boolean resolved;

    public FeedbackResponse(Long id, Long userId, Long scooterId, String content, String priority, Boolean resolved) {
        this.id = id;
        this.userId = userId;
        this.scooterId = scooterId;
        this.content = content;
        this.priority = priority;
        this.resolved = resolved;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getScooterId() { return scooterId; }
    public String getContent() { return content; }
    public String getPriority() { return priority; }
    public Boolean getResolved() { return resolved; }
}
