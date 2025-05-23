package com.example.storeclothes.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Category;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private static CategoryRepository instance;
    private final FirebaseFirestore firestore;

    private CategoryRepository() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public static CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }
    public LiveData<List<Category>> getCategories() {
        MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
        
        firestore.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> categories = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Category category = doc.toObject(Category.class);
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                    categoriesLiveData.setValue(categories);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu cần
                    categoriesLiveData.setValue(new ArrayList<>());
                });
                
        return categoriesLiveData;
    }
    public LiveData<Category> getCategoryById(String categoryId) {
        MutableLiveData<Category> categoryLiveData = new MutableLiveData<>();
        
        firestore.collection("categories")
                .document(categoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Category category = documentSnapshot.toObject(Category.class);
                        categoryLiveData.setValue(category);
                    } else {
                        categoryLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu cần
                    categoryLiveData.setValue(null);
                });
                
        return categoryLiveData;
    }
    
    /**
     * Thêm danh mục mới
     * @param category Đối tượng danh mục cần thêm
     * @return LiveData<Boolean> kết quả thêm danh mục (true: thành công, false: thất bại)
     */
    public LiveData<Boolean> addCategory(Category category) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("categories")
                .document(category.getCategoryId())
                .set(category)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));
                
        return resultLiveData;
    }
}