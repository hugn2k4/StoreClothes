package com.example.storeclothes.ui.Profile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.service.UserService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class InformationActivity extends AppCompatActivity {

    private FloatingActionButton fabBack;
    private MaterialButton btnSave,btnUpload;
    private ShapeableImageView imageViewAvata;
    private TextInputEditText edtFirstname, edtLastname, edtEmail, edtAddress, edtPhonenumber;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initViews();
        setupClickListeners();
        loadUserInfo();
    }
    private void initViews() {
        fabBack = findViewById(R.id.fabBack);
        btnSave = findViewById(R.id.btnSave);
        btnUpload = findViewById(R.id.btnUpload);
        edtFirstname = findViewById(R.id.edtFirstname);
        edtLastname = findViewById(R.id.edtLastname);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhonenumber = findViewById(R.id.edtPhonenumber);
        imageViewAvata = findViewById(R.id.imageViewAvata);
    }
    private void setupClickListeners() {
        fabBack.setOnClickListener(view -> finish());
        btnSave.setOnClickListener(view -> saveUserInfo());
//        btnUpload.setOnClickListener(view -> showImagePickerDialog());
    }
    private void loadUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getUserById(userId, new UserService.OnUserFetchListener() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                edtFirstname.setText(user.getFirstName());
                edtLastname.setText(user.getLastName());
                edtEmail.setText(user.getEmail());
                edtAddress.setText(user.getAddress());
                edtPhonenumber.setText(user.getPhone());
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(InformationActivity.this, "Lỗi tải thông tin: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu từ giao diện
        String firstName = edtFirstname.getText().toString().trim();
        String lastName = edtLastname.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhonenumber.getText().toString().trim();

        // Cập nhật vào đối tượng currentUser
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setAddress(address);
        currentUser.setPhone(phone);

        // Gửi lên Firestore
        UserService.getInstance().updateUser(currentUser, new UserService.OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(InformationActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InformationActivity.this, "Lỗi khi cập nhật: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}