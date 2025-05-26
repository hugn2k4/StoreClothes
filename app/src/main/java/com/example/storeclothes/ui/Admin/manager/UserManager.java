package com.example.storeclothes.ui.Admin.manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.User;
import com.example.storeclothes.ui.Admin.strategy.DataLoadStrategy;
import com.example.storeclothes.ui.Admin.strategy.UserDataLoadStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserManager extends BaseManager<User> {
    private final FirebaseFirestore firestore;
    private final DataLoadStrategy<User> loadStrategy;

    public UserManager() {
        this.firestore = FirebaseFirestore.getInstance();
        this.loadStrategy = new UserDataLoadStrategy();
    }

    @Override
    public LiveData<List<User>> getAll() {
        return loadStrategy.loadData();
    }

    public LiveData<User> getById(String id) {
        return loadStrategy.loadById(id);
    }

    @Override
    public LiveData<Boolean> add(User user) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));
                
        return resultLiveData;
    }

    @Override
    public LiveData<Boolean> update(User user) {
        return add(user); // Cùng logic với add
    }

    @Override
    public LiveData<Boolean> delete(String id) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("users")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));
                
        return resultLiveData;
    }
}