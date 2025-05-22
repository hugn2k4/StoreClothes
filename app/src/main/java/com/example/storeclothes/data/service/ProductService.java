package com.example.storeclothes.data.service;
import com.example.storeclothes.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private static ProductService instance;
    private final FirebaseFirestore firestore;

    private ProductService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    public void addProduct(Product product, OnProductAddListener listener) {
        firestore.collection("products")
                .document(product.getProductId())
                .set(product)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ProductService", "Failed to add product", e);
                    listener.onFailure(e.getMessage());
                });
    }
    
    public void updateProduct(Product product, OnProductUpdateListener listener) {
        firestore.collection("products")
                .document(product.getProductId())
                .set(product)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ProductService", "Failed to update product", e);
                    listener.onFailure(e.getMessage());
                });
    }
    
    public void deleteProduct(String productId, OnProductDeleteListener listener) {
        firestore.collection("products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("ProductService", "Failed to delete product", e);
                    listener.onFailure(e.getMessage());
                });
    }
    public void getProductListFromFirebase(OnProductListListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển DocumentSnapshot thành Product object
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        listener.onSuccess(productList);
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }
    public void getProductById(String productId, OnProductDetailListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .document(productId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Product product = task.getResult().toObject(Product.class);
                        listener.onSuccess(product);
                    } else {
                        listener.onFailure(task.getException() != null ? task.getException() : new Exception("Product not found"));
                    }
                });
    }
    public interface OnProductAddListener {
        void onSuccess();
        void onFailure(String error);
    }
    
    public interface OnProductUpdateListener {
        void onSuccess();
        void onFailure(String error);
    }
    
    public interface OnProductDeleteListener {
        void onSuccess();
        void onFailure(String error);
    }
    
    public interface OnProductListListener {
        void onSuccess(List<Product> products);
        void onFailure(Exception e);
    }
    public interface OnProductDetailListener {
        void onSuccess(Product product);
        void onFailure(Exception e);
    }
}