package com.example.storeclothes.ui.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.service.UserService;
import com.example.storeclothes.ui.Order.OrderActivity;
import com.example.storeclothes.ui.Profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private String userUid;
    private UserService userService;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userUid = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_uid", null);
        userService = UserService.getInstance();
        initViews();
        loadUserData();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_notification) {
                openActivity(HomeActivity.class);
            } else if (itemId == R.id.nav_order) {
                openActivity(OrderActivity.class);
            } else if (itemId == R.id.nav_profile) {
                openActivity(ProfileActivity.class);
            }
            return true;
        });
    }
    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
    private void loadUserData() {
        if (userUid != null && !userUid.isEmpty()) {
            userService.getUserById(userUid, new UserService.OnUserFetchListener() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        tvGreeting.setText("Hi, " + user.getFirstName() );
                    }
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(HomeActivity.this, "Không thể lấy thông tin người dùng: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvGreeting.setText("Hi, Guest");
        }
    }
    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}