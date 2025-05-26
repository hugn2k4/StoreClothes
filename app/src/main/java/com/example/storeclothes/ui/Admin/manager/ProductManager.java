package com.example.storeclothes.ui.Admin.manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Admin.strategy.DataLoadStrategy;
import com.example.storeclothes.ui.Admin.strategy.ProductDataLoadStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductManager extends BaseManager<Product> {
    private final FirebaseFirestore firestore;
    private final DataLoadStrategy<Product> loadStrategy;

    public ProductManager() {
        this.firestore = FirebaseFirestore.getInstance();
        this.loadStrategy = new ProductDataLoadStrategy();
    }

    @Override
    public LiveData<List<Product>> getAll() {
        return loadStrategy.loadData();
    }

    @Override
    public LiveData<Product> getById(String id) {
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
    public LiveData<Boolean> add(Product product) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("products")
                .document(product.getProductId())
                .set(product)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));
                
        return resultLiveData;
    }

    @Override
    public LiveData<Boolean> update(Product product) {
        return add(product); // Cùng logic với add
    }

    @Override
    public LiveData<Boolean> delete(String id) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("products")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));
                
        return resultLiveData;
    }
}