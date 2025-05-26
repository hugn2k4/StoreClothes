package com.example.storeclothes.ui.Admin.strategy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserDataLoadStrategy implements DataLoadStrategy<User> {
    private final FirebaseFirestore firestore;

    public UserDataLoadStrategy() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public LiveData<List<User>> loadData() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        
        firestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    usersLiveData.setValue(users);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    usersLiveData.setValue(new ArrayList<>());
                });
                
        return usersLiveData;
    }
    @Override
    public LiveData<User> loadById(String id) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();

        firestore.collection("users")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.setValue(user);
                    } else {
                        userLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> userLiveData.setValue(null));

        return userLiveData;
    }

    @Override
    public String getStrategyName() {
        return "User Data Loading Strategy";
    }
}