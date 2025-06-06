package com.example.storeclothes.data.model;

import java.util.Date;

public class Review {
    private String reviewId;
    private String userId;
    private String productId;
    private double rating;
    private String comment;
    private Date date;
    private String userName;
    private String avatarUrl;

    public Review(){}
    private Review(Builder builder) {
        this.reviewId = builder.reviewId;
        this.userId = builder.userId;
        this.productId = builder.productId;
        this.rating = builder.rating;
        this.comment = builder.comment;
        this.date = builder.date;
        this.userName = builder.userName;
        this.avatarUrl = builder.avatarUrl;
    }

    // Chỉ getter, không có setter (immutable)
    public String getReviewId() { return reviewId; }
    public String getUserId() { return userId; }
    public String getProductId() { return productId; }
    public double getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getDate() { return date; }
    public String getUserName() { return userName; }
    public String getAvatarUrl() { return avatarUrl; }

    public static class Builder {
        private String reviewId;
        private String userId;
        private String productId;
        private double rating;
        private String comment;
        private Date date;
        private String userName;
        private String avatarUrl;

        public Builder() {}

        public Builder setReviewId(String reviewId) {
            this.reviewId = reviewId;
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

        public Builder setRating(double rating) {
            this.rating = rating;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Review build() {
            return new Review(this);
        }
    }
}
