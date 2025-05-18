package com.example.storeclothes.data.model;

public class Wishlist {
    private String wishlistId;
    private String userId;
    private String productId;

    // Constructor
    public Wishlist(String wishlistId, String userId, String productId) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.productId = productId;
    }
    public Wishlist() {
    }

    // Getter and Setter methods
    public String getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(String wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
