package com.example.storeclothes.data.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseFirestoreSingleton {
    private static FirebaseFirestore instance;
    private FirebaseFirestoreSingleton() {}
    public static FirebaseFirestore getInstance() {
        if (instance == null) {
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }
}