package com.example.storeclothes.data.service;

import android.util.Log;

import com.example.storeclothes.data.firebase.FirebaseFirestoreSingleton;

import com.example.storeclothes.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserService {
    private static UserService instance;
    private final FirebaseFirestore firestore;

    private UserService() {
        this.firestore = FirebaseFirestoreSingleton.getInstance();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public void getUserById(String userId, OnUserFetchListener listener) {
        if (userId == null || userId.isEmpty()) {
            listener.onFailure("Invalid user ID");
            return;
        }
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure("Failed to convert document to User");
                        }
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure("User not found");
                    }
                })
                .addOnFailureListener(e ->listener.onFailure(e.getMessage()));
    }

    public void updateUser(User user, OnUserUpdateListener listener) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public interface OnUserFetchListener {
        void onSuccess(User user);
        void onFailure(String error);
    }

    public interface OnUserUpdateListener {
        void onSuccess();
        void onFailure(String error);
    }
}