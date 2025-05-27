package com.example.storeclothes.data.model;

import java.util.List;

public class Product {
    private String productId;
    private String name;
    private String description;
    private Double price;
    private String categoryId;
    private List<String> images;

    private Product(Builder builder) {
        this.productId = builder.productId;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.categoryId = builder.categoryId;
        this.images = builder.images;
    }
    public Product() {
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public List<String> getImages() {
        return images;
    }

    // Builder class
    public static class Builder {
        private String productId;
        private String name;
        private String description;
        private Double price;
        private String categoryId;
        private List<String> images;

        public Builder() {}

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder categoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder images(List<String> images) {
            this.images = images;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
