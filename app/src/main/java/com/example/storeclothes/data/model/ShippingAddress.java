package com.example.storeclothes.data.model;

public class ShippingAddress {
    private String addressId;
    private String orderId;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String postalCode;

    // Constructor
    public ShippingAddress(String addressId, String orderId, String fullName, String phone, String addressLine, String city, String postalCode) {
        this.addressId = addressId;
        this.orderId = orderId;
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.postalCode = postalCode;
    }

    // Getter and Setter methods
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
