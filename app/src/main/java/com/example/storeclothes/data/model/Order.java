package com.example.storeclothes.data.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> items;
    private double totalAmount;
    private double shippingFee;
    private String status;
    private Date orderDate;

    // Constructor cho Firestore
    public Order() {}

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.userId = builder.userId;
        this.items = builder.items;
        this.totalAmount = builder.totalAmount;
        this.shippingFee = builder.shippingFee;
        this.status = builder.status;
        this.orderDate = builder.orderDate;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public double getShippingFee() { return shippingFee; }
    public String getStatus() { return status; }
    public Date getOrderDate() { return orderDate; }

    // Builder pattern
    public static class Builder {
        private String orderId = UUID.randomUUID().toString();
        private String userId;
        private List<OrderItem> items;
        private double totalAmount;
        private double shippingFee;
        private String status;
        private Date orderDate;

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setItems(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder setShippingFee(double shippingFee) {
            this.shippingFee = shippingFee;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setOrderDate(Date orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
