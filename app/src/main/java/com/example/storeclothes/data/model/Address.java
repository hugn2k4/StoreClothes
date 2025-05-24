package com.example.storeclothes.data.model;

public class Address {
    private String id;
    private String address;

    public Address() { }
    private Address(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
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
    public static class Builder {
        private String id;
        private String address;

        public Builder() { }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
