package com.example.storeclothes.ui.Customer.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.ui.Customer.ViewModel.InformationViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class InformationActivity extends AppCompatActivity {

    private FloatingActionButton fabBack;
    private MaterialButton btnSave, btnUpload;
    private ShapeableImageView imageViewAvata;
    private TextInputEditText edtFirstname, edtLastname, edtEmail, edtAddress, edtPhonenumber;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private InformationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        viewModel = new ViewModelProvider(this).get(InformationViewModel.class);
        initViews();
        setupClickListeners();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.loadUser(uid);
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                edtFirstname.setText(user.getFirstName());
                edtLastname.setText(user.getLastName());
                edtEmail.setText(user.getEmail());
                edtAddress.setText(user.getAddress());
                edtPhonenumber.setText(user.getPhone());

                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Glide.with(this).load(user.getAvatarUrl()).into(imageViewAvata);
                }
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getLoading().observe(this, loading -> {
            setButtonsEnabled(!loading);
            if (loading) {
                Toast.makeText(this, "Đang xử lý...", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
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


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(this).load(selectedImageUri).into(imageViewAvata);
                    }
                });
    }

    private void setupClickListeners() {
        fabBack.setOnClickListener(v -> finish());
        btnUpload.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void saveUserInfo() {
        User user = viewModel.getCurrentUser().getValue();
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setFirstName(edtFirstname.getText().toString().trim());
        user.setLastName(edtLastname.getText().toString().trim());
        user.setAddress(edtAddress.getText().toString().trim());
        user.setPhone(edtPhonenumber.getText().toString().trim());

        viewModel.updateUser(user, selectedImageUri, this);
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void setButtonsEnabled(boolean enabled) {
        btnUpload.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        fabBack.setEnabled(enabled);
    }
}
