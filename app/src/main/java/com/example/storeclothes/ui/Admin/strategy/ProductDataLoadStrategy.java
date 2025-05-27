package com.example.storeclothes.ui.Admin.strategy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductDataLoadStrategy implements DataLoadStrategy<Product> {
    private final FirebaseFirestore firestore;

    public ProductDataLoadStrategy() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public LiveData<List<Product>> loadData() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        firestore.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        products.add(product);
                    }
                    productsLiveData.setValue(products);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    productsLiveData.setValue(new ArrayList<>());
                });
                
        return productsLiveData;
    }
    @Override
    public LiveData<Product> loadById(String id) {
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();

        firestore.collection("products")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        productLiveData.setValue(product);
                    } else {
                        productLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> productLiveData.setValue(null));

        return productLiveData;
    }

    @Override
    public String getStrategyName() {
        return "Product Data Loading Strategy";
    }
}