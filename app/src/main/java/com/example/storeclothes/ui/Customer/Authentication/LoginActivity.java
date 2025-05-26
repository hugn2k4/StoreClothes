package com.example.storeclothes.ui.Customer.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeclothes.R;
import com.example.storeclothes.data.local.LocalStorageManager;
import com.example.storeclothes.ui.Customer.Home.HomeActivity;
import com.example.storeclothes.ui.Customer.ViewModel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton btnContinue;
    private TextView txtCreateAccount;
    private TextInputEditText edtEmail;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LocalStorageManager storage = new LocalStorageManager(this);
        if (storage.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        initViews();
        setClickListeners();
        observeViewModel();
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
        }
        else{
            authViewModel.checkEmailExists(email, exists -> {
                if (exists) {
                    Intent intent = new Intent(LoginActivity.this, PasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Email chưa được đăng ký", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void observeViewModel() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
        });
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}