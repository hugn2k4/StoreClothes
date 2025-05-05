package com.example.storeclothes.data.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseFirestoreSingleton {
    private static FirebaseFirestore instance;

    // Private constructor để ngăn tạo đối tượng mới
    private FirebaseFirestoreSingleton() {}

    // Lấy đối tượng FirebaseFirestore duy nhất
    public static FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }
}