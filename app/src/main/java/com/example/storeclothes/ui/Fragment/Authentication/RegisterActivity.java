package com.example.storeclothes.ui.Fragment.Authentication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.ViewModel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private MaterialButton btnContinue;
    private TextView txtResetPassword;
    private TextInputEditText edtFirstname, edtLastname, edtEmail, edtPassword, edtAddress, edtPhone;
    private FloatingActionButton fabBack;
    private CircularProgressIndicator progressIndicator;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupObservers();
        setClickListeners();
    }

    private void initViews() {
        btnContinue = findViewById(R.id.btnContinue);
        edtFirstname = findViewById(R.id.edtFirstname);
        edtLastname = findViewById(R.id.edtLastname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        txtResetPassword = findViewById(R.id.txtResetPassword);
        fabBack = findViewById(R.id.fabBack);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void setupObservers() {
        // Quan sát trạng thái loading
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // Hiển thị loading indicator
                progressIndicator.setVisibility(View.VISIBLE);
                btnContinue.setText(""); // Ẩn text khi hiển thị loading
                btnContinue.setEnabled(false);
            } else {
                // Ẩn loading indicator
                progressIndicator.setVisibility(View.GONE);
                btnContinue.setText("Continue");
                btnContinue.setEnabled(true);
            }
        });

        // Quan sát thông báo lỗi
        authViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát kết quả đăng ký
        authViewModel.getRegistrationSuccess().observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại màn hình đăng nhập
            }
        });
    }

    private void setClickListeners() {
        btnContinue.setOnClickListener(v -> handleRegister());
        fabBack.setOnClickListener(v -> finish());
        txtResetPassword.setOnClickListener(v -> {
            // Xử lý reset password nếu cần
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleRegister() {
        String firstname = edtFirstname.getText().toString().trim();
        String lastname = edtLastname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Gọi phương thức đăng ký trong ViewModel
        authViewModel.registerUser(firstname, lastname, email, password, address, phone);
    }
}