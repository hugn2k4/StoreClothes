package com.example.storeclothes.ui.Customer.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.Category;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.repository.CategoryRepository;
import com.example.storeclothes.data.repository.ProductRepository;
import com.example.storeclothes.data.repository.UserRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    
    public HomeViewModel() {
        // Sử dụng Singleton pattern cho các repository
        this.productRepository = ProductRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
        this.categoryRepository = CategoryRepository.getInstance();
    }

    public LiveData<User> getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    public LiveData<List<Product>> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public LiveData<Product> getProductById(String productId) {return productRepository.getProductById(productId);}

    public LiveData<List<Category>> getCategories() {
        return categoryRepository.getCategories();
    }

    public LiveData<Category> getCategoryById(String categoryId) {return categoryRepository.getCategoryById(categoryId);}
}