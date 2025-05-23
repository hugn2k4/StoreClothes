package com.example.storeclothes.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static UserRepository instance;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private UserRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public LiveData<List<User>> getCustomers() {
        loadUsers();
        return usersLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void loadUsers() {
        isLoading.setValue(true);
        
        firestore.collection("users")
                // Bỏ điều kiện lọc theo role để hiển thị tất cả người dùng
                .orderBy("lastName", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    List<User> customers = new ArrayList<>();
                    
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            customers.add(user);
                        }
                    }
                    
                    usersLiveData.setValue(customers);
                    isLoading.setValue(false);
                });
    }

    public void refreshCustomers() {
        loadUsers();
    }
    
    // Xóa người dùng
    public void deleteUser(User user, OnUserActionListener listener) {
        isLoading.setValue(true);
        
        firestore.collection("users")
                .document(user.getUserId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    isLoading.setValue(false);
                    listener.onSuccess("Đã xóa người dùng thành công");
                    refreshCustomers();
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    listener.onFailure("Không thể xóa người dùng: " + e.getMessage());
                });
    }
    
    // Cập nhật trạng thái người dùng (vô hiệu hóa/kích hoạt)
    public void updateUserStatus(User user, String newStatus, OnUserActionListener listener) {
        isLoading.setValue(true);
        
        user.setStatus(newStatus);
        
        firestore.collection("users")
                .document(user.getUserId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    isLoading.setValue(false);
                    String message = newStatus.equals("active") ? 
                            "Đã kích hoạt người dùng thành công" : 
                            "Đã vô hiệu hóa người dùng thành công";
                    listener.onSuccess(message);
                    refreshCustomers();
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    listener.onFailure("Không thể cập nhật trạng thái người dùng: " + e.getMessage());
                });
    }
    
    // Interface callback cho các hành động trên người dùng
    public interface OnUserActionListener {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }
}