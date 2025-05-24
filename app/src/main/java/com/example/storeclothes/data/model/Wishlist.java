package com.example.storeclothes.data.model;

public class Wishlist {
    private String wishlistId;
    private String userId;
    private String productId;

    public Wishlist() {
    }
    private Wishlist(Builder builder) {
        this.wishlistId = builder.wishlistId;
        this.userId = builder.userId;
        this.productId = builder.productId;
    }
    public String getWishlistId() {
        return wishlistId;
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

    public static class Builder {
        private String wishlistId;
        private String userId;
        private String productId;

        public Builder() {}

        public Builder setWishlistId(String wishlistId) {
            this.wishlistId = wishlistId;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public Wishlist build() {
            return new Wishlist(this);
        }
    }
}
