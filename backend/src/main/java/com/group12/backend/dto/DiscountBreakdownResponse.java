package com.group12.backend.dto;

import java.math.BigDecimal;

/**
 * TODO(ID22): 折扣明细返回体骨架。
 */
public class DiscountBreakdownResponse {

    private String discountType;
    private BigDecimal originalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private String messageKey;

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public static DiscountBreakdownResponse of(BigDecimal originalPrice,
                                               String discountType,
                                               BigDecimal discountAmount,
                                               BigDecimal finalPrice,
                                               String messageKey) {
        // TODO(ID22): add validation for negative/null values.
        DiscountBreakdownResponse response = new DiscountBreakdownResponse();
        response.setOriginalPrice(originalPrice);
        response.setDiscountType(discountType);
        response.setDiscountAmount(discountAmount);
        response.setFinalPrice(finalPrice);
        response.setMessageKey(messageKey);
        return response;
    }
}
