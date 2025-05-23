package com.example.storeclothes.ui.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.local.LocalStorageManager;
import com.example.storeclothes.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel cho các màn hình xác thực (Authentication)
 * Xử lý logic nghiệp vụ và quản lý trạng thái UI
 */
public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private LocalStorageManager storage = new LocalStorageManager(getApplication());
    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();
        
        // Khởi tạo người dùng hiện tại nếu đã đăng nhập
        FirebaseUser user = authRepository.getCurrentUser();
        if (user != null) {
            currentUser.setValue(user);
        }
    }
    
    // Getter cho LiveData
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }
    
    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }
    
    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Đăng ký người dùng mới
     */
    public void registerUser(String firstName, String lastName, String email, String password, String address, String phone) {
        // Kiểm tra dữ liệu đầu vào
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() 
                || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            errorMessage.setValue("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        
        if (password.length() < 6) {
            errorMessage.setValue("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        
        // Bắt đầu quá trình đăng ký
        isLoading.setValue(true);
        
        authRepository.registerUser(firstName, lastName, email, password, address, phone)
                .observeForever(result -> {
                    isLoading.setValue(false);
                    
                    if (result.isSuccess()) {
                        registrationSuccess.setValue(true);
                    } else {
                        errorMessage.setValue(result.getErrorMessage());
                    }
                });
    }
    
    /**
     * Đăng nhập người dùng
     */
    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Vui lòng nhập email và mật khẩu");
            return;
        }
        
        isLoading.setValue(true);
        
        authRepository.loginUser(email, password)
                .observeForever(result -> {
                    isLoading.setValue(false);
                    
                    if (result.isSuccess()) {
                        FirebaseUser user = result.getUser();
                        currentUser.setValue(user);
                        loginSuccess.setValue(true);
                        
                        // Lưu thông tin đăng nhập vào SharedPreferences
                        if (user != null) {
                            storage.saveLoginState(user.getUid());
                        }
                    } else {
                        errorMessage.setValue(result.getErrorMessage());
                    }
                });
    }
    public void checkEmailExists(String email, EmailCheckListener listener) {
        if (email.isEmpty()) {
            errorMessage.setValue("Vui lòng nhập email");
            return;
        }
        
        isLoading.setValue(true);
        
        authRepository.checkEmailExists(email)
                .observeForever(exists -> {
                    isLoading.setValue(false);
                    listener.onEmailExists(exists);
                });
    }
    
    /**
     * Interface cho việc kiểm tra email
     */
    public interface EmailCheckListener {
        void onEmailExists(boolean exists);
    }
    
    /**
     * Đăng xuất người dùng hiện tại
     */
    public void logout() {
        authRepository.logout();
        
        // Xóa thông tin đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        
        currentUser.setValue(null);
    }
}