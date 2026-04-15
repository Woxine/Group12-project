package com.group12.backend.dto;

/**
 * 用于返回预约订单的详情、状态和起止位置信息。
 */
public class BookingResponse {
    private String id;
    private String scooterId;
    private String userId;
    private String status;
    private String createdAt;
    private String startTime;
    private String endTime;
    private String duration;
    private Double totalPrice;
    private Double originalPrice;
    private Double discountAmount;
    private Double discountMultiplier;
    private String discountType;
    private Double startLat;
    private Double startLng;
    private Double endLat;
    private Double endLng;

    public BookingResponse() {
    }

    public BookingResponse(String id, String scooterId, String userId, String status, String createdAt) {
        this.id = id;
        this.scooterId = scooterId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getScooterId() { return scooterId; }
    public void setScooterId(String scooterId) { this.scooterId = scooterId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public Double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Double originalPrice) { this.originalPrice = originalPrice; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public Double getDiscountMultiplier() { return discountMultiplier; }
    public void setDiscountMultiplier(Double discountMultiplier) { this.discountMultiplier = discountMultiplier; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public Double getStartLat() { return startLat; }
    public void setStartLat(Double startLat) { this.startLat = startLat; }
    public Double getStartLng() { return startLng; }
    public void setStartLng(Double startLng) { this.startLng = startLng; }
    public Double getEndLat() { return endLat; }
    public void setEndLat(Double endLat) { this.endLat = endLat; }
    public Double getEndLng() { return endLng; }
    public void setEndLng(Double endLng) { this.endLng = endLng; }
}
