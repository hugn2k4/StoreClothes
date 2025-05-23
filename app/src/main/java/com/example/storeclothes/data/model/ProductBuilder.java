package com.example.storeclothes.data.model;

import java.util.List;

public class ProductBuilder {
    private String productId;
    private String name;
    private String description;
    private Double price;
    private String categoryId;
    private List<String> images;

    public ProductBuilder setProductId(String productId) {
        this.productId = productId;
        return this;
    }
    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }
    public ProductBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    public ProductBuilder setPrice(Double price) {
        this.price = price;
        return this;
    }
    public ProductBuilder setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }
    public ProductBuilder setImages(List<String> images) {
        this.images = images;
        return this;
    }
    public Product build() {
        return new Product(productId, name, description, price, categoryId, images);
    }
}