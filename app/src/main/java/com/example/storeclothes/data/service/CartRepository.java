package com.example.storeclothes.data.service;

import com.example.storeclothes.data.model.Cart;
import com.example.storeclothes.data.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private final FirebaseFirestore db;
    private final FirebaseUser user;

    public CartRepository() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void addToCart(CartItem newItem, OnCartUpdateListener listener) {
        if (user == null) {
            listener.onFailure("Người dùng chưa đăng nhập.");
            return;
        }

        String userId = user.getUid();
        DocumentReference cartRef = db.collection("carts").document(userId);

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Cart existingCart = documentSnapshot.toObject(Cart.class);
                List<CartItem> items = existingCart != null ? existingCart.getCartItems() : new ArrayList<>();

                boolean found = false;
                for (int i = 0; i < items.size(); i++) {
                    CartItem item = items.get(i);
                    if (item.getProductId().equals(newItem.getProductId())
                            && item.getSize().equals(newItem.getSize())
                            && item.getColor().equals(newItem.getColor())) {
                        int updatedQuantity = item.getQuantity() + newItem.getQuantity();
                        items.set(i, new CartItem.Builder()
                                .setProductId(item.getProductId())
                                .setQuantity(updatedQuantity)
                                .setSize(item.getSize())
                                .setColor(item.getColor())
                                .build());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    items.add(newItem);
                }

                Cart updatedCart = new Cart.Builder()
                        .setUserId(userId)
                        .setCartItems(items)
                        .build();

                cartRef.set(updatedCart)
                        .addOnSuccessListener(unused -> listener.onSuccess())
                        .addOnFailureListener(e -> listener.onFailure("Lỗi khi cập nhật giỏ hàng"));

            } else {
                List<CartItem> items = new ArrayList<>();
                items.add(newItem);
                Cart newCart = new Cart.Builder()
                        .setUserId(userId)
                        .setCartItems(items)
                        .build();

                cartRef.set(newCart)
                        .addOnSuccessListener(unused -> listener.onSuccess())
                        .addOnFailureListener(e -> listener.onFailure("Lỗi khi tạo giỏ hàng"));
            }
        }).addOnFailureListener(e -> listener.onFailure("Lỗi khi truy vấn giỏ hàng"));
    }

    public interface OnCartUpdateListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
