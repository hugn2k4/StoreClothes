package com.example.storeclothes.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static OrderRepository instance;
    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }
    public interface OrderCallback {
        void onOrdersLoaded(List<Order> orders, List<String> productIds);
    }

    public interface ProductCallback {
        void onProductsLoaded(Map<String, Product> productMap);
    }

    public void getOrdersByUserId(String userId, OrderCallback callback) {
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Order> orderList = new ArrayList<>();
                    List<String> productIds = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        try {
                            Order order = doc.toObject(Order.class);
                            if (order != null) {
                                order.setOrderId(doc.getId());
                                orderList.add(order);

                                if (order.getItems() != null) {
                                    for (OrderItem item : order.getItems()) {
                                        if (item.getCartItem() != null) {
                                            productIds.add(item.getCartItem().getProductId());
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {}
                    }

                    callback.onOrdersLoaded(orderList, productIds);
                });
    }

    public void getProductsByIds(List<String> productIds, ProductCallback callback) {
        db.collection("products")
                .whereIn("productId", productIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Product> productMap = new HashMap<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        try {
                            Product product = doc.toObject(Product.class);
                            if (product != null && product.getProductId() != null) {
                                productMap.put(product.getProductId(), product);
                            }
                        } catch (Exception ignored) {}
                    }

                    callback.onProductsLoaded(productMap);
                });
    }
}
