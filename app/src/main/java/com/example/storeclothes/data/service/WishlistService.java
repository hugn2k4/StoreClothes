package com.example.storeclothes.data.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.storeclothes.data.model.Wishlist;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class WishlistService {
    private static WishlistService instance;
    private final FirebaseFirestore firestore;

    private WishlistService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static WishlistService getInstance() {
        if (instance == null) {
            instance = new WishlistService();
        }
        return instance;
    }

    // Thêm sản phẩm vào wishlist
    public void addToWishlist(@NonNull Wishlist wishlist, @NonNull OnWishlistActionListener listener) {
        firestore.collection("wishlist")
                .document(wishlist.getWishlistId())
                .set(wishlist)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("WishlistService", "Add to wishlist failed", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // Xoá sản phẩm khỏi wishlist
    public void removeFromWishlist(String wishlistId, @NonNull OnWishlistActionListener listener) {
        firestore.collection("wishlist")
                .document(wishlistId)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("WishlistService", "Remove from wishlist failed", e);
                    listener.onFailure(e.getMessage());
                });
    }
    public void removeAllFromWishlist(String userUid, @NonNull OnWishlistActionListener listener) {
        firestore.collection("wishlist")
                .whereEqualTo("userId", userUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = firestore.batch();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener(unused -> listener.onSuccess())
                            .addOnFailureListener(e -> {
                                Log.e("WishlistService", "Remove all from wishlist failed", e);
                                listener.onFailure(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("WishlistService", "Get wishlist to remove all failed", e);
                    listener.onFailure(e.getMessage());
                });
    }


    // Lấy danh sách wishlist của người dùng
    public void getWishlistByUser(String userId, @NonNull OnWishlistListListener listener) {
        firestore.collection("wishlist")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Wishlist> wishlists = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Wishlist wishlist = doc.toObject(Wishlist.class);
                        wishlists.add(wishlist);
                    }
                    listener.onSuccess(wishlists);
                })
                .addOnFailureListener(e -> {
                    Log.e("WishlistService", "Get wishlist failed", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // Listener cho thao tác thêm/xoá
    public interface OnWishlistActionListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // Listener lấy danh sách wishlist
    public interface OnWishlistListListener {
        void onSuccess(List<Wishlist> wishlists);
        void onFailure(String errorMessage);
    }
}
