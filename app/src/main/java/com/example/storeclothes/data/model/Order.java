package com.example.storeclothes.data.model;

import java.util.Date;

public class Order {
    private String orderId;
    private String userId;
    private Date orderDate;
    private String status;
    private Double totalAmount;
    private Long paymentMethodId;
    private String voucherId;

    // Constructor không tham số cho Firestore
    public Order() {
        // Constructor không tham số cần thiết cho Firebase Firestore
    }

    // Constructor
    public Order(String orderId, String userId, Date orderDate, String status, Double totalAmount, Long paymentMethodId, String voucherId) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentMethodId = paymentMethodId;
        this.voucherId = voucherId;
    }

    // Getter and Setter methods
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }
}
