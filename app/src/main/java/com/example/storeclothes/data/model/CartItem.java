package com.example.storeclothes.data.model;

public class CartItem {
    private String productId;
    private int quantity;
    private String size;
    private String color;

    private CartItem(Builder builder) {
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.size = builder.size;
        this.color = builder.color;
    }

    public static class Builder {
        private String productId;
        private int quantity;
        private String size;
        private String color;

        public Builder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public CartItem build() {
            return new CartItem(this);
        }
    }

    public CartItem() {}

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSize() { return size; }
    public String getColor() { return color; }
}
