package com.example.storeclothes.ui.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.service.UserService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InformationActivity extends AppCompatActivity {

    private FloatingActionButton fabBack;
    private MaterialButton btnSave, btnUpload;
    private ShapeableImageView imageViewAvata;
    private TextInputEditText edtFirstname, edtLastname, edtEmail, edtAddress, edtPhonenumber;
    private User currentUser;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;

    private static final String IMAGEKIT_UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload";
    private static final String IMAGEKIT_PUBLIC_KEY = "public_xxoDxA/rmR/ItuATyOU5k5c/slE=";
    private static final String IMAGEKIT_PRIVATE_KEY = "private_IwsI+3E5LZq5gDM7d8wYhq0VIhQ="; // Encode to Base64 for Authorization

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

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imageViewAvata.setImageURI(selectedImageUri);
                        uploadImageToImageKit(selectedImageUri);
                    }
                }
        );
    }

    private void setupClickListeners() {
        fabBack.setOnClickListener(v -> finish());
        btnUpload.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void loadUserInfo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getUserById(uid, new UserService.OnUserFetchListener() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                edtFirstname.setText(user.getFirstName());
                edtLastname.setText(user.getLastName());
                edtEmail.setText(user.getEmail());
                edtAddress.setText(user.getAddress());
                edtPhonenumber.setText(user.getPhone());

                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Glide.with(InformationActivity.this).load(user.getAvatarUrl()).into(imageViewAvata);
                }
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
        currentUser.setFirstName(edtFirstname.getText().toString().trim());
        currentUser.setLastName(edtLastname.getText().toString().trim());
        currentUser.setAddress(edtAddress.getText().toString().trim());
        currentUser.setPhone(edtPhonenumber.getText().toString().trim());

        UserService.getInstance().updateUser(currentUser, new UserService.OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(InformationActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(InformationActivity.this, "Cập nhật thất bại: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void uploadImageToImageKit(Uri uri) {
        try {
            setButtonsEnabled(false);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "Không thể mở ảnh", Toast.LENGTH_SHORT).show();
                setButtonsEnabled(true);
                return;
            }

            byte[] imageBytes = getBytes(inputStream);
            String base64Image = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", base64Image)
                    .addFormDataPart("fileName", "avatar_" + System.currentTimeMillis() + ".jpg")
                    .addFormDataPart("publicKey", IMAGEKIT_PUBLIC_KEY)
                    .build();

            String credential = Base64.encodeToString((IMAGEKIT_PRIVATE_KEY + ":").getBytes(), Base64.NO_WRAP);

            Request request = new Request.Builder()
                    .url(IMAGEKIT_UPLOAD_URL)
                    .addHeader("Authorization", "Basic " + credential)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(InformationActivity.this, "Upload ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setButtonsEnabled(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.body() == null) {
                            runOnUiThread(() -> {
                                Toast.makeText(InformationActivity.this, "Phản hồi rỗng từ máy chủ", Toast.LENGTH_SHORT).show();
                                setButtonsEnabled(true);
                            });
                            return;
                        }

                        String result = response.body().string();
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = new JSONObject(result);

                            if (!jsonObject.has("url")) {
                                runOnUiThread(() -> {
                                    Toast.makeText(InformationActivity.this, "Phản hồi không chứa URL ảnh", Toast.LENGTH_SHORT).show();
                                    setButtonsEnabled(true);
                                });
                                return;
                            }

                            String url = jsonObject.getString("url");
                            currentUser.setAvatarUrl(url);

                            UserService.getInstance().updateUser(currentUser, new UserService.OnUserUpdateListener() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        Glide.with(InformationActivity.this).load(url).into(imageViewAvata);
                                        Toast.makeText(InformationActivity.this, "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
                                        setButtonsEnabled(true);
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(InformationActivity.this, "Cập nhật ảnh thất bại: " + error, Toast.LENGTH_SHORT).show();
                                        setButtonsEnabled(true);
                                    });
                                }
                            });

                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(InformationActivity.this, "Upload thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                                setButtonsEnabled(true);
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(InformationActivity.this, "Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                            setButtonsEnabled(true);
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
            setButtonsEnabled(true);
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
    private void setButtonsEnabled(boolean enabled) {
        btnUpload.setEnabled(enabled);
        btnSave.setEnabled(enabled);
        fabBack.setEnabled(enabled);
    }
}
