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
}