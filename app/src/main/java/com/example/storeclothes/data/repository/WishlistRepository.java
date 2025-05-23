package com.example.storeclothes.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Wishlist;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WishlistRepository {
    private static WishlistRepository instance;
    private final FirebaseFirestore firestore;

    private WishlistRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized WishlistRepository getInstance() {
        if (instance == null) {
            instance = new WishlistRepository();
        }
        return instance;
    }

    public LiveData<List<Wishlist>> getWishlistByUser(String userId) {
        MutableLiveData<List<Wishlist>> wishlistsLiveData = new MutableLiveData<>();
        firestore.collection("wishlist")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        wishlistsLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    List<Wishlist> wishlists = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot doc : querySnapshot) {
                            Wishlist wishlist = doc.toObject(Wishlist.class);
                            if (wishlist != null) {
                                wishlists.add(wishlist);
                            }
                        }
                    }
                    wishlistsLiveData.setValue(wishlists);
                });
        return wishlistsLiveData;
    }

    public LiveData<Boolean> checkWishlistStatus(String userId, String productId) {
        MutableLiveData<Boolean> statusLiveData = new MutableLiveData<>();
        String wishlistId = userId + "_" + productId;
        firestore.collection("wishlist")
                .document(wishlistId)
                .get()
                .addOnSuccessListener(documentSnapshot -> statusLiveData.setValue(documentSnapshot.exists()))
                .addOnFailureListener(e -> statusLiveData.setValue(false));
        return statusLiveData;
    }

    public Task<Void> addToWishlist(Wishlist wishlist) {
        return firestore.collection("wishlist").document(wishlist.getWishlistId()).set(wishlist);
    }

    public Task<Void> removeFromWishlist(String wishlistId) {
        return firestore.collection("wishlist").document(wishlistId).delete();
    }
}