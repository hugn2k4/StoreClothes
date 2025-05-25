package com.example.storeclothes.ui.Fragment.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Fragment.Admin.ManagerHomeActivity;
import com.example.storeclothes.ui.Fragment.Home.HomeActivity;
import com.example.storeclothes.ui.ViewModel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class PasswordActivity extends AppCompatActivity {

    private FloatingActionButton fabBack;
    private TextInputEditText edtPassword;
    private MaterialButton btnContinue;
    private TextView txtResetPassword;
    private AuthViewModel authViewModel;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        email = getIntent().getStringExtra("email");

        authViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(AuthViewModel.class);

        initViews();
        observeViewModel();
        setClickListeners();
    }

    private void initViews() {
        fabBack = findViewById(R.id.fabBack);
        edtPassword = findViewById(R.id.edtPassword);
        btnContinue = findViewById(R.id.btnContinue);
        txtResetPassword = findViewById(R.id.txtResetPassword);
    }

    private void setClickListeners() {
        fabBack.setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> {
            String password = edtPassword.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.loginUser(email, password);
        });

        txtResetPassword.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình reset mật khẩu
        });
    }

    private void observeViewModel() {
        authViewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                // Kiểm tra role của người dùng
                authViewModel.getCurrentUser().observe(this, user -> {
                    if (user != null) {
                        authViewModel.checkUserRole(user.getUid(), role -> {
                            if (role != null && role.equals("MANAGER")) {
                                Toast.makeText(this, "Đăng nhập thành công với quyền quản lý!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, ManagerHomeActivity.class));
                            } else {
                                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomeActivity.class));
                            }
                            finish();
                        });
                    }
                });
            }
        });

        // Giữ nguyên các observer khác
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            // Xử lý loading indicator nếu cần
        });
    }
}
