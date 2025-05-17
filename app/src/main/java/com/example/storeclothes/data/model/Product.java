package com.example.storeclothes.data.model;

import java.util.List;

public class Product {
    private String productId;
    private String name;
    private String description;
    private Double price;
    private String categoryId;
    private List<String> images;

    public Product() { }

    public Product(String productId, String name, String description, Double price, String categoryId, List<String> images) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.images = images;
    }

    // getter và setter các trường
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
