package com.example.storeclothes.data.firebase;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthSingleton {
    private static FirebaseAuth instance;

    // Private constructor để ngăn tạo đối tượng mới
    private FirebaseAuthSingleton() {}

    // Lấy đối tượng FirebaseAuth duy nhất
    public static FirebaseAuth getInstance() {
        if (instance == null) {
            instance = FirebaseAuth.getInstance();
        }
        return instance;
    }
}