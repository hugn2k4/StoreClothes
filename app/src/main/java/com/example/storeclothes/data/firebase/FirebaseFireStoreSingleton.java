package com.example.storeclothes.data.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseFireStoreSingleton {
    private static FirebaseFirestore instance;
    private FirebaseFireStoreSingleton() {}

    public static FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }
}