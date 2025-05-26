package com.example.storeclothes.ui.Customer.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final ProductRepository repository;
    private final MutableLiveData<String> categoryId = new MutableLiveData<>();
    private final MutableLiveData<String> keyword = new MutableLiveData<>("");
    private final LiveData<List<Product>> allProducts;

    private final MediatorLiveData<List<Product>> filteredProducts = new MediatorLiveData<>();

    public CategoryViewModel() {
        repository = ProductRepository.getInstance();

        allProducts = Transformations.switchMap(categoryId, id -> {
            if (id == null || id.isEmpty()) {
                MutableLiveData<List<Product>> empty = new MutableLiveData<>();
                empty.setValue(new ArrayList<>());
                return empty;
            }
            return repository.getProductsByCategory(id);
        });

        // Cập nhật filteredProducts khi allProducts hoặc keyword thay đổi
        filteredProducts.addSource(allProducts, products -> filterProducts(products, keyword.getValue()));
        filteredProducts.addSource(keyword, k -> filterProducts(allProducts.getValue(), k));
    }

    private void filterProducts(List<Product> products, String k) {
        if (products == null) {
            filteredProducts.setValue(new ArrayList<>());
            return;
        }
        if (k == null) k = "";
        String key = k.toLowerCase();
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(key)) {
                filtered.add(p);
            }
        }
        filteredProducts.setValue(filtered);
    }

    public LiveData<List<Product>> getFilteredProducts() {
        return filteredProducts;
    }

    public void setCategoryId(String id) {
        categoryId.setValue(id);
    }

    public void setKeyword(String kw) {
        keyword.setValue(kw);
    }
}