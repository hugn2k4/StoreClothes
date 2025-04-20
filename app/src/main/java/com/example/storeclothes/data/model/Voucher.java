package com.example.storeclothes.data.model;

import java.util.Date;

public class Voucher {
    private String voucherId;
    private String code;
    private Double discountAmount;
    private Date expiryDate;

    // Constructor
    public Voucher(String voucherId, String code, Double discountAmount, Date expiryDate) {
        this.voucherId = voucherId;
        this.code = code;
        this.discountAmount = discountAmount;
        this.expiryDate = expiryDate;
    }

    // Getter and Setter methods
    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
