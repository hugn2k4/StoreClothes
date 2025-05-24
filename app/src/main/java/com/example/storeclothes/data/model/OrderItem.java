package com.example.storeclothes.data.model;

public class OrderItem {
    private CartItem cartItem;
    private boolean reviewed;

    public OrderItem() {}

    private OrderItem(Builder builder) {
        this.cartItem = builder.cartItem;
        this.reviewed = builder.reviewed;
    }

    public OrderItem(CartItem cartItem) {
        this.cartItem = cartItem;
        this.reviewed = false;
    }

    public CartItem getCartItem() { return cartItem; }
    public boolean isReviewed() { return reviewed; }
    public void setReviewed(boolean reviewed) { this.reviewed = reviewed; }

    public static class Builder {
        private CartItem cartItem;
        private boolean reviewed = false;

        public Builder() {}

        public Builder setCartItem(CartItem cartItem) {
            this.cartItem = cartItem;
            return this;
        }

        public Builder setReviewed(boolean reviewed) {
            this.reviewed = reviewed;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
