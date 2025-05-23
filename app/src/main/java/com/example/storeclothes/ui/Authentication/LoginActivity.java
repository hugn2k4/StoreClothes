package com.example.storeclothes.ui.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storeclothes.data.firebase.Authentication;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Home.HomeActivity;
import com.example.storeclothes.ui.Manager.ManagerActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton btnContinue;
    private TextView txtCreateAccount;
    private TextInputEditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            // Kiểm tra vai trò người dùng để chuyển hướng đến màn hình phù hợp
            String userRole = sharedPreferences.getString("user_role", "CUSTOMER");
            Intent intent;
            if ("MANAGER".equals(userRole)) {
                intent = new Intent(LoginActivity.this, ManagerActivity.class);
            } else {
                // Mặc định là CUSTOMER hoặc vai trò khác
                intent = new Intent(LoginActivity.this, HomeActivity.class);
            }
            startActivity(intent);
            finish();
        }
        initViews();
        setClickListeners();
    }
    private void initViews() {
        btnContinue = findViewById(R.id.btnContinue);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        edtEmail = findViewById(R.id.edtEmail);
    }
    private void setClickListeners(){
        btnContinue.setOnClickListener(v -> handleLogin());
        txtCreateAccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }
    private void handleLogin() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        existsEmail(email);
    }
    private void existsEmail(String email) {
        Authentication.getInstance().checkEmailExists(this, email, exists -> {
            if (exists) {
                Intent intent = new Intent(LoginActivity.this, PasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Email chưa được đăng ký", Toast.LENGTH_SHORT).show();
            }
        });
    }
}