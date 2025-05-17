package com.example.storeclothes.data.service;

import android.util.Log;

import com.example.storeclothes.data.model.Category;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private static CategoryService instance;
    private final FirebaseFirestore firestore;

    private CategoryService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    public void addCategory(Category category, OnCategoryAddListener listener) {
        firestore.collection("categories")
                .document(category.getCategoryId())
                .set(category)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("CategoryService", "Failed to add category", e);
                    listener.onFailure(e.getMessage());
                });
    }

    public void getCategoryListFromFirebase(OnCategoryListListener listener) {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category> categoryList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Category category = document.toObject(Category.class);
                            categoryList.add(category);
                        }
                        listener.onSuccess(categoryList);
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public interface OnCategoryAddListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnCategoryListListener {
        void onSuccess(List<Category> categories);
        void onFailure(Exception e);
    }
}
