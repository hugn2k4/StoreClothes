package com.example.storeclothes.data.model;

public class OrderItem {
    private CartItem cartItem;
    private boolean reviewed;
    public OrderItem() {}
    public OrderItem(CartItem cartItem) {
        this.cartItem = cartItem;
        this.reviewed = false;
    }
    public OrderItem(CartItem cartItem, boolean reviewed) {
        this.cartItem = cartItem;
        this.reviewed = reviewed;
    }
    public CartItem getCartItem() {
        return cartItem;
    }
    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
    public boolean isReviewed() {
        return reviewed;
    }
    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
}
