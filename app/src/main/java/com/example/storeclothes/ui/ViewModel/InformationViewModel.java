package com.example.storeclothes.ui.ViewModel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.repository.UserRepository;

public class InformationViewModel extends ViewModel {
    private final UserRepository userRepository = UserRepository.getInstance();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<User> getCurrentUser() { return currentUser; }

    public void loadUser(String userId) {
        isLoading.setValue(true);
        userRepository.getUserById(userId).observeForever(user -> {
            isLoading.postValue(false);
            if (user != null) currentUser.postValue(user);
            else errorMessage.postValue("Không tìm thấy người dùng");
        });
    }

    public void updateUser(User user, Uri imageUri, Context context) {
        isLoading.setValue(true);
        if (imageUri != null) {
            // Upload ảnh trước
            userRepository.uploadImageToImageKit(imageUri, context).observeForever(url -> {
                if (url != null) {
                    user.setAvatarUrl(url);
                    updateUserInFirestore(user);
                } else {
                    errorMessage.postValue("Upload ảnh thất bại");
                    isLoading.postValue(false);
                }
            });
        } else {
            updateUserInFirestore(user);
        }
    }

    private void updateUserInFirestore(User user) {
        userRepository.updateUser(user).observeForever(success -> {
            isLoading.postValue(false);
            if (success) currentUser.postValue(user);
            else errorMessage.postValue("Cập nhật thất bại");
        });
    }
}
