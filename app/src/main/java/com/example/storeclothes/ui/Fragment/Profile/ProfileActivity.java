package com.example.storeclothes.ui.Fragment.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.local.LocalStorageManager;
import com.example.storeclothes.ui.Fragment.Authentication.LoginActivity;
import com.example.storeclothes.ui.Fragment.Home.HomeActivity;
import com.example.storeclothes.ui.Fragment.Order.OrderActivity;
import com.example.storeclothes.ui.ViewModel.UserViewModel;
import com.example.storeclothes.ui.Fragment.Wishlist.WishlistActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private String userUid;
    private FloatingActionButton fabBack;
    private TextView textSignOut, tvName;
    private MaterialButton btnInformation, btnWishlist, btnLanguage, btnSetting, btnSupport;
    private ImageView imageViewAvata;
    private BottomNavigationView bottomNavigationView;
    private UserViewModel userViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userViewModel = new UserViewModel();
        loadUserData();
        initViews();
        setClickListeners();
    }
    private void loadUserData() {
        if (userUid != null && !userUid.isEmpty()) {
            userViewModel.getUserById(userUid).observe(this, user -> {
                if (user != null) {
                    tvName.setText(user.getLastName() + " " + user.getFirstName());
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(user.getAvatarUrl())
                                .placeholder(R.drawable.ic_loading)
                                .into(imageViewAvata);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
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
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    public void setClickListeners() {
        textSignOut.setOnClickListener(v -> handleSignOut());
        fabBack.setOnClickListener(v -> finish());
        btnWishlist.setOnClickListener(v -> openActivity(WishlistActivity.class));
        btnInformation.setOnClickListener(v -> openActivity(InformationActivity.class));
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                openActivity(HomeActivity.class);
            } else if (itemId == R.id.nav_notification) {
                openActivity(HomeActivity.class);
            } else if (itemId == R.id.nav_order) {
                openActivity(OrderActivity.class);
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return true;
        });
    }
    public void handleSignOut(){
        LocalStorageManager storage = new LocalStorageManager(getApplication());
        storage.clearLoginState();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}