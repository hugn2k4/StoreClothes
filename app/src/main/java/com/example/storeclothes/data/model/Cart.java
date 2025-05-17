package com.example.storeclothes.data.model;

public class Cart {
    private String cartId;
    private String userId;

    // Constructor
    public Cart(String cartId, String userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    // Getter for cartId
    public String getCartId() {
        return cartId;
    }

    // Setter for cartId
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
