package com.example.storeclothes.data.model;

public class Category {
    private String categoryId;
    private String name;
    private String imageUrl;

    // Constructor private để bắt buộc tạo đối tượng qua Builder
    private Category(Builder builder) {
        this.categoryId = builder.categoryId;
        this.name = builder.name;
        this.imageUrl = builder.imageUrl;
    }
    public Category() {}

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Builder class
    public static class Builder {
        private String categoryId;
        private String name;
        private String imageUrl;

        public Builder setCategoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Category build() {
            return new Category(this);
        }
    }
}
