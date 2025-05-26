package com.example.storeclothes.ui.Customer.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository = UserRepository.getInstance();

    public LiveData<User> getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    public LiveData<Boolean> updateUser(User user) {
        return userRepository.updateUser(user);
    }
}
