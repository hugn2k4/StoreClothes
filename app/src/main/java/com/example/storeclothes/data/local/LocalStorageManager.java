package com.example.storeclothes.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorageManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_UID = "user_uid";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences sharedPreferences;

    public LocalStorageManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLoginState(String uid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UID, uid);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserUid() {
        return sharedPreferences.getString(KEY_UID, null);
    }

    public void clearLoginState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}