package com.example.storeclothes.data.model;

public class Address {
    private String id; // id document Firestore, không phải auto-increment kiểu Room
    private String address;

    // Constructor rỗng cần thiết cho Firebase
    public Address() {
    }

    public Address(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}