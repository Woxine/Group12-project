package com.group12.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feedbacks")
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "scooter_id")
    private Scooter scooter;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'LOW'")
    private String priority; // LOW, HIGH

    private Boolean resolved = false;

    @Column(columnDefinition = "BIT(1) DEFAULT b'0'")
    private Boolean escalated = false;

    @Column(name = "escalated_to", length = 100)
    private String escalatedTo;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;

    @ManyToOne
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "process_note", columnDefinition = "TEXT")
    private String processNote;

    @Column(name = "escalation_status", length = 32)
    private String escalationStatus = "PENDING";

    public Feedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Scooter getScooter() { return scooter; }
    public void setScooter(Scooter scooter) { this.scooter = scooter; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }

    public Boolean getEscalated() { return escalated; }
    public void setEscalated(Boolean escalated) { this.escalated = escalated; }

    public String getEscalatedTo() { return escalatedTo; }
    public void setEscalatedTo(String escalatedTo) { this.escalatedTo = escalatedTo; }

    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }

    public User getProcessedBy() { return processedBy; }
    public void setProcessedBy(User processedBy) { this.processedBy = processedBy; }

    public String getProcessNote() { return processNote; }
    public void setProcessNote(String processNote) { this.processNote = processNote; }

    public String getEscalationStatus() { return escalationStatus; }
    public void setEscalationStatus(String escalationStatus) { this.escalationStatus = escalationStatus; }
}
