package com.example.storeclothes.ui.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.service.UserService;
import com.example.storeclothes.ui.Authentication.LoginActivity;
import com.example.storeclothes.ui.Home.HomeActivity;
import com.example.storeclothes.ui.Wishlist.WishlistActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private String userUid;
    private UserService userService;
    private FloatingActionButton fabBack;
    private TextView textSignOut, tvName;
    private MaterialButton btnInformation, btnWishlist, btnLanguage, btnSetting, btnSupport;
    private ImageView imageViewAvata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userService = UserService.getInstance();
        loadUserData();
        initViews();
        setClickListeners();
    }
    private void loadUserData() {
        if (userUid != null && !userUid.isEmpty()) {
            userService.getUserById(userUid, new UserService.OnUserFetchListener() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        tvName.setText(user.getLastName() + " " + user.getFirstName());
                    }
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(user.getAvatarUrl())
                                .placeholder(R.drawable.ic_loading)
                                .into(imageViewAvata);
                    }
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(ProfileActivity.this, "Không thể lấy thông tin người dùng: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void initViews() {
        tvName = findViewById(R.id.tvName);
        textSignOut = findViewById(R.id.textSignOut);
        fabBack = findViewById(R.id.fabBack);
        btnWishlist = findViewById(R.id.btnWishlist);
        btnInformation = findViewById(R.id.btnInformation);
        imageViewAvata = findViewById(R.id.imageViewAvata);
    }
    public void setClickListeners() {
        textSignOut.setOnClickListener(v -> handleSignOut());
        fabBack.setOnClickListener(v -> finish());
        btnWishlist.setOnClickListener(v -> openActivity(WishlistActivity.class));
        btnInformation.setOnClickListener(v -> openActivity(InformationActivity.class));
    }
    public void handleSignOut(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}