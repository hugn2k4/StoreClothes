package com.example.storeclothes.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.storeclothes.data.firebase.FirebaseAuthSingleton;
import com.example.storeclothes.data.firebase.FirebaseFirestoreSingleton;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.ui.Authentication.LoginActivity;
import com.example.storeclothes.ui.Authentication.PasswordActivity;
import com.example.storeclothes.ui.Home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Authentication {
    private static final String TAG = "Authentication";
    private static Authentication instance;

    // Sử dụng Singleton FirebaseAuth và FirebaseFirestore
    private final FirebaseAuth firebaseAuth = FirebaseAuthSingleton.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestoreSingleton.getInstance();

    // Private constructor để ngăn tạo nhiều đối tượng
    private Authentication() {}

    // getInstance để áp dụng Singleton
    public static Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    // Phương thức đăng ký người dùng và lưu vào Firestore
    public void registerUser(Context context, String email, String password, String firstName, String lastName, String address, String phone) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công -> Lấy UID
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        // Tạo đối tượng User
                        User user = new User(
                                userId,
                                firstName,
                                lastName,
                                email,
                                password,
                                new Date(),
                                false,
                                "CUSTOMER",
                                address,
                                phone
                        );
                        // Ghi dữ liệu lên Firestore
                        firestore.collection("users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                })
                                .addOnFailureListener(e -> {
                                    // Nếu ghi Firestore thất bại → Xoá tài khoản đã tạo
                                    firebaseAuth.getCurrentUser().delete()
                                            .addOnCompleteListener(deleteTask -> {
                                                Toast.makeText(context, "Lỗi khi lưu thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                            });
                                });
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        Log.e("Authen", "Đăng ký thất bại: " + errorMsg);
                        Toast.makeText(context, "Đăng ký thất bại: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void checkEmailExists(Context context, String email, EmailCheckCallback callback) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            callback.onCheckComplete(true);
                        } else {
                            callback.onCheckComplete(false);
                        }
                    } else {
                        Toast.makeText(context, "Lỗi kiểm tra email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        callback.onCheckComplete(false);
                    }
                });
    }

    public interface EmailCheckCallback {
        void onCheckComplete(boolean exists);
    }

    public void loginUser(Context context, String email, String password, LoginCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Gọi callback với user khi đăng nhập thành công
                            callback.onLoginSuccess(user);
                        } else {
                            // Nếu không có người dùng
                            callback.onLoginFailure("Người dùng không hợp lệ.");
                        }
                    } else {
                        // Đăng nhập thất bại
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        callback.onLoginFailure("Đăng nhập thất bại: " + errorMsg);
                    }
                });
    }
    public interface LoginCallback {
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(String errorMessage);
    }
}
