package com.example.storeclothes.ui.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.storeclothes.firebase.Authentication;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Home.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class PasswordActivity extends AppCompatActivity {

    private FloatingActionButton fabBack;
    private TextInputEditText edtPassword;
    private MaterialButton btnContinue;
    private TextView txtResetPassword;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        email = getIntent().getStringExtra("email");
        initViews();
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
                Toast.makeText(PasswordActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }
            handleLogin(email, password);
        });
        txtResetPassword.setOnClickListener(v -> {
            // Handle reset password text click
        });
    }
    private void handleLogin(String email, String password) {
        Authentication.getInstance().loginUser(PasswordActivity.this, email, password, new Authentication.LoginCallback() {
            @Override
            public void onLoginSuccess(FirebaseUser user) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_uid",  user.getUid());
                editor.putBoolean("is_logged_in", true);
                editor.apply();

                Toast.makeText(PasswordActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PasswordActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onLoginFailure(String errorMessage) {
                Toast.makeText(PasswordActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}