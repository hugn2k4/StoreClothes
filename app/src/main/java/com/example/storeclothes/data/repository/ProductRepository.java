package com.example.storeclothes.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductRepository {
    private static ProductRepository instance;
    private final FirebaseFirestore firestore;

    private ProductRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

        firestore.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            products.add(product);
                        }
                    }
                    productsLiveData.setValue(products);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu cần
                    productsLiveData.setValue(new ArrayList<>());
                });

        return productsLiveData;
    }

    public LiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();

        firestore.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        productLiveData.setValue(product);
                    } else {
                        productLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    productLiveData.setValue(null);
                });

        return productLiveData;
    }
    public LiveData<List<Product>> getProductsByCategory(String categoryId) {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        firestore.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    for (var doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        productList.add(product);
                    }
                    liveData.postValue(productList);
                })
                .addOnFailureListener(e -> {
                    liveData.postValue(new ArrayList<>());
                });
        return liveData;
    }
    public LiveData<Boolean> addProduct(Product product) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        firestore.collection("products")
                .document(product.getProductId())
                .set(product)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));

        return resultLiveData;
    }
    public void getWishlistByUser(String userId, Consumer<List<Wishlist>> onSuccess, Consumer<String> onFailure) {
        firestore.collection("wishlists")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Wishlist> wishlists = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Wishlist wishlist = doc.toObject(Wishlist.class);
                        if (wishlist != null) {
                            wishlists.add(wishlist);
                        }
                    }
                    onSuccess.accept(wishlists);
                })
                .addOnFailureListener(e -> onFailure.accept(e.getMessage()));
    }
    public void getProductsByIds(List<String> productIds, Consumer<List<Product>> onSuccess, Consumer<String> onFailure) {
        if (productIds.isEmpty()) {
            onSuccess.accept(new ArrayList<>());
            return;
        }
        firestore.collection("products")
                .whereIn("productId", productIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            products.add(product);
                        }
                    }
                    onSuccess.accept(products);
                })
                .addOnFailureListener(e -> onFailure.accept(e.getMessage()));
    }
    public Task<Void> addToWishlist(Wishlist wishlist) {
        return firestore.collection("wishlists").document(wishlist.getWishlistId()).set(wishlist);
    }

    public Task<Void> removeFromWishlist(String wishlistId) {
        return firestore.collection("wishlists").document(wishlistId).delete();
    }
}