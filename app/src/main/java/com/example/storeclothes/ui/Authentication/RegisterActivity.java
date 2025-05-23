package com.example.storeclothes.ui.Authentication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.storeclothes.data.firebase.Authentication;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storeclothes.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private MaterialButton btnContinue;
    private TextView txtResetPassword;
    private TextInputEditText edtFirstname,edtLastname,edtEmail,edtPassword,edtAddress,edtPhone;
    private FloatingActionButton fabBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
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
    }
    private void setClickListeners(){
        btnContinue.setOnClickListener(v -> handleRegister());
        fabBack.setOnClickListener(v -> finish());
    }
    private void handleRegister() {
        String firstname = edtFirstname.getText().toString().trim();
        String lastname = edtLastname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty()
                || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký người dùng mới với vai trò mặc định là CUSTOMER
        Authentication.getInstance().registerUser(this, email, password, firstname, lastname, address, phone);
    }
}