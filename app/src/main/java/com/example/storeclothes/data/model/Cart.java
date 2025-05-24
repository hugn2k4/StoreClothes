package com.example.storeclothes.data.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String userId;
    private List<CartItem> cartItems;

    private Cart(Builder builder) {
        this.userId = builder.userId;
        this.cartItems = builder.cartItems;
    }

    public static class Builder {
        private String userId;
        private List<CartItem> cartItems = new ArrayList<>();

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            return this;
        }

        public Builder addItem(CartItem item) {
            this.cartItems.add(item);
            return this;
        }

        public Cart build() {
            return new Cart(this);
        }
    }
    public Cart() {}

    public String getUserId() { return userId; }
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

}
