package com.example.storeclothes.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.firebase.FirebaseAuthSingleton;
import com.example.storeclothes.data.firebase.FirebaseFireStoreSingleton;
import com.example.storeclothes.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

/**
 * Repository xử lý tất cả các tương tác với Firebase Authentication và Firestore
 * liên quan đến xác thực người dùng.
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;
    
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    
    private AuthRepository() {
        this.firebaseAuth = FirebaseAuthSingleton.getInstance();
        this.firestore = FirebaseFireStoreSingleton.getInstance();
    }
    
    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }
    public LiveData<AuthResult> registerUser(String firstName, String lastName, String email, 
                                           String password, String address, String phone) {
        MutableLiveData<AuthResult> resultLiveData = new MutableLiveData<>();
        
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            // Tạo đối tượng User
                            User user = new User.Builder()
                                    .setUserId(userId)
                                    .setFirstName(firstName)
                                    .setLastName(lastName)
                                    .setEmail(email)
                                    .setPassword(password)
                                    .setAddress(address)
                                    .setPhone(phone)
                                    .build();
                            
                            // Lưu thông tin người dùng vào Firestore
                            saveUserToFirestore(user, resultLiveData);
                        } else {
                            resultLiveData.setValue(new AuthResult(false, "Lỗi tạo tài khoản người dùng"));
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        resultLiveData.setValue(new AuthResult(false, "Đăng ký thất bại: " + errorMsg));
                    }
                });
                
        return resultLiveData;
    }
    
    private void saveUserToFirestore(User user, MutableLiveData<AuthResult> resultLiveData) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    resultLiveData.setValue(new AuthResult(true, null));
                })
                .addOnFailureListener(e -> {
                    // Nếu ghi Firestore thất bại → Xoá tài khoản đã tạo
                    FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
                    if (currentFirebaseUser != null) {
                        currentFirebaseUser.delete();
                    }
                    resultLiveData.setValue(new AuthResult(false, "Lỗi khi lưu thông tin người dùng: " + e.getMessage()));
                });
    }
    public LiveData<AuthResult> loginUser(String email, String password) {
        MutableLiveData<AuthResult> resultLiveData = new MutableLiveData<>();
        
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            resultLiveData.setValue(new AuthResult(true, null, user));
                        } else {
                            resultLiveData.setValue(new AuthResult(false, "Người dùng không hợp lệ"));
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        resultLiveData.setValue(new AuthResult(false, "Đăng nhập thất bại: " + errorMsg));
                    }
                });
                
        return resultLiveData;
    }
    public LiveData<Boolean> checkEmailExists(String email) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            resultLiveData.setValue(true);
                        } else {
                            resultLiveData.setValue(false);
                        }
                    } else {
                        Log.e(TAG, "Lỗi kiểm tra email: " + 
                                (task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định"));
                        resultLiveData.setValue(false);
                    }
                });
                
        return resultLiveData;
    }
    public void logout() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static class AuthResult {
        private final boolean success;
        private final String errorMessage;
        private final FirebaseUser user;
        
        public AuthResult(boolean success, String errorMessage) {
            this(success, errorMessage, null);
        }
        
        public AuthResult(boolean success, String errorMessage, FirebaseUser user) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public FirebaseUser getUser() {
            return user;
        }
    }
}