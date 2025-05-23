package com.example.storeclothes.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Address;
import com.example.storeclothes.data.model.Cart;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CartRepository {
    private static CartRepository instance;
    private final FirebaseFirestore db;

    public CartRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public Task<Void> addToCart(String userId, CartItem item) {
        DocumentReference cartRef = db.collection("carts").document(userId);
        return cartRef.get().continueWithTask(documentSnapshotTask -> {
            if (!documentSnapshotTask.isSuccessful()) {
                return Tasks.forException(documentSnapshotTask.getException());
            }

            List<CartItem> cartItems;
            if (documentSnapshotTask.getResult().exists()) {
                Cart cart = documentSnapshotTask.getResult().toObject(Cart.class);
                cartItems = cart != null ? cart.getCartItems() : new ArrayList<>();
            } else {
                cartItems = new ArrayList<>();
                // Create a new cart if it doesn't exist
                Cart newCart = new Cart();
                newCart.setCartItems(cartItems);
                cartRef.set(newCart);
            }

            // Check if item already exists in cart
            boolean itemExists = false;
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem existingItem = cartItems.get(i);
                if (existingItem.getProductId().equals(item.getProductId()) &&
                        existingItem.getSize().equals(item.getSize()) &&
                        existingItem.getColor().equals(item.getColor())) {
                    existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                    cartItems.set(i, existingItem);
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                cartItems.add(item);
            }

            return cartRef.update("cartItems", cartItems);
        });
    }
    public LiveData<List<CartItem>> getCartItems(String userId) {
        MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>();
        DocumentReference cartRef = db.collection("carts").document(userId);
        cartRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                cartItemsLiveData.setValue(new ArrayList<>());
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Cart cart = snapshot.toObject(Cart.class);
                cartItemsLiveData.setValue(cart != null && cart.getCartItems() != null ? cart.getCartItems() : new ArrayList<>());
            } else {
                cartItemsLiveData.setValue(new ArrayList<>());
            }
        });
        return cartItemsLiveData;
    }

    public void getProductsByIds(List<String> productIds, Consumer<Map<String, Product>> onSuccess, Consumer<String> onFailure) {
        if (productIds.isEmpty()) {
            onSuccess.accept(new HashMap<>());
            return;
        }
        db.collection("products")
                .whereIn("productId", productIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Product> productMap = new HashMap<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Product product = doc.toObject(Product.class);
                        if (product != null && product.getProductId() != null) {
                            productMap.put(product.getProductId(), product);
                        }
                    }
                    onSuccess.accept(productMap);
                })
                .addOnFailureListener(e -> onFailure.accept(e.getMessage()));
    }

    public LiveData<List<Address>> getAddresses() {
        MutableLiveData<List<Address>> addressesLiveData = new MutableLiveData<>();
        db.collection("addresses")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        addressesLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    List<Address> addressList = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot doc : querySnapshot) {
                            Address address = doc.toObject(Address.class);
                            if (address != null) {
                                address.setId(doc.getId());
                                addressList.add(address);
                            }
                        }
                    }
                    addressesLiveData.setValue(addressList);
                });
        return addressesLiveData;
    }

    public Task<DocumentReference> addAddress(Address address) {
        return db.collection("addresses").add(address);
    }

    public Task<DocumentReference> placeOrder(Order order) {
        return db.collection("orders").add(order);
    }

    public Task<Void> updateCartItems(String userId, List<CartItem> cartItems) {
        DocumentReference cartRef = db.collection("carts").document(userId);
        return cartRef.set(new Cart.Builder()
                .setUserId(userId)
                .setCartItems(cartItems)
                .build());
    }

    public Task<Void> clearCart(String userId) {
        DocumentReference cartRef = db.collection("carts").document(userId);
        return cartRef.update("cartItems", new ArrayList<>());
    }

}
