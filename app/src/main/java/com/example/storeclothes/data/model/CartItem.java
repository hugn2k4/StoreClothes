package com.example.storeclothes.data.model;

public class CartItem {
    private String productId;
    private int quantity;
    private String size;
    private String color;

    // Constructor private để ép dùng Builder
    private CartItem(Builder builder) {
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.size = builder.size;
        this.color = builder.color;
    }

    // Dành riêng cho Firebase
    public CartItem() {}

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getSize() { return size; }
    public String getColor() { return color; }

    /** @deprecated Only for Firebase deserialization */
    @Deprecated
    public void setQuantity(int quantity) { this.quantity = quantity; }

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
}
