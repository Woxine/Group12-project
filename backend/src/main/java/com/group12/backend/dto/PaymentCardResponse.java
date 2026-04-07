package com.group12.backend.dto;

/**
 * TODO(ID2): 支付卡返回体骨架。
 */
public class PaymentCardResponse {
    private String id;
    private String brand;
    private String maskedNumber;
    private Boolean isDefault;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getMaskedNumber() { return maskedNumber; }
    public void setMaskedNumber(String maskedNumber) { this.maskedNumber = maskedNumber; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
