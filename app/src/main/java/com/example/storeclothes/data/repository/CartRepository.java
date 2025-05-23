package com.example.storeclothes.data.repository;

import com.example.storeclothes.data.model.Cart;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Order;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CartRepository {

    private final FirebaseFirestore db;
    private static CartRepository instance;
    private CartRepository() {
        db = FirebaseFirestore.getInstance(); // private constructor
    }
    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public void getCartItems(String userId, Consumer<List<CartItem>> onResult) {
        db.collection("carts").document(userId)
            .get()
            .addOnSuccessListener(snapshot -> {
                Cart cart = snapshot.toObject(Cart.class);
                onResult.accept(cart != null ? cart.getCartItems() : new ArrayList<>());
            })
            .addOnFailureListener(e -> onResult.accept(new ArrayList<>()));
    }

    public Task<DocumentSnapshot> getCart(String userId) {
        return db.collection("carts").document(userId).get();
    }

    public Task<Void> clearCart(String userId) {
        return db.collection("carts").document(userId)
                .update("cartItems", new ArrayList<>());
    }

    public Task<Void> placeOrder(Order order, String userId) {
        return db.collection("orders")
                .add(order)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return clearCart(userId);
                    }
                    throw task.getException();
                });
    }

    public Task<Void> addToCart(String userId, CartItem item) {
        return getCart(userId)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    DocumentSnapshot document = task.getResult();
                    Cart cart = document.toObject(Cart.class);
                    if (cart == null) {
                        cart = new Cart.Builder()
                                .setUserId(userId)
                                .addItem(item)
                                .build();
                    } else {
                        cart.getCartItems().add(item);
                    }

                    return db.collection("carts").document(userId).set(cart);
                });
    }

    public Task<Void> updateCartItem(String userId, CartItem updatedItem) {
        return getCart(userId)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    DocumentSnapshot document = task.getResult();
                    Cart cart = document.toObject(Cart.class);
                    if (cart == null) {
                        throw new Exception("Cart not found");
                    }

                    List<CartItem> items = cart.getCartItems();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getProductId().equals(updatedItem.getProductId())) {
                            items.set(i, updatedItem);
                            break;
                        }
                    }

                    return db.collection("carts").document(userId).set(cart);
                });
    }
}