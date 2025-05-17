package com.example.storeclothes.ui.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Authentication.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView textSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();
        setClickListeners();
    }
    public void initViews() {
        textSignOut = findViewById(R.id.textSignOut);
    }
    public void setClickListeners() {
        textSignOut.setOnClickListener(v -> handleSignOut());
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
}