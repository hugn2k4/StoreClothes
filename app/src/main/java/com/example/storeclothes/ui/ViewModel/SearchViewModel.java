package com.example.storeclothes.ui.ViewModel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final ProductRepository repository;
    private final MutableLiveData<String> keyword = new MutableLiveData<>("");
    private final MutableLiveData<Float> minPrice = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> maxPrice = new MutableLiveData<>(1000f);

    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>(new ArrayList<>());

    public final MediatorLiveData<List<Product>> filteredProducts = new MediatorLiveData<>();

    public SearchViewModel() {
        repository = ProductRepository.getInstance();

        repository.getAllProducts().observeForever(products -> {
            allProducts.postValue(products != null ? products : new ArrayList<>());
        });

        // Khi bất kỳ dữ liệu nào thay đổi, gọi lại hàm filter
        filteredProducts.addSource(allProducts, products -> filterAndPostResult());
        filteredProducts.addSource(keyword, k -> filterAndPostResult());
        filteredProducts.addSource(minPrice, min -> filterAndPostResult());
        filteredProducts.addSource(maxPrice, max -> filterAndPostResult());
    }

    private void filterAndPostResult() {
        List<Product> products = allProducts.getValue();
        if (products == null) {
            filteredProducts.postValue(new ArrayList<>());
            return;
        }
        String k = keyword.getValue() == null ? "" : keyword.getValue().toLowerCase();
        float min = minPrice.getValue() == null ? 0f : minPrice.getValue();
        float max = maxPrice.getValue() == null ? 1000f : maxPrice.getValue();

        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(k)) {
                double price = p.getPrice();
                if (price >= min && price <= max) {
                    filtered.add(p);
                }
            }
        }
        filteredProducts.postValue(filtered);
    }

    public void setKeyword(String k) {
        keyword.postValue(k);
    }

    public void setPriceRange(float min, float max) {
        minPrice.postValue(min);
        maxPrice.postValue(max);
    }
}
