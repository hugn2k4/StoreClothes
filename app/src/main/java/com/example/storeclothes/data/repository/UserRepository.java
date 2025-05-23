package com.example.storeclothes.data.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.firebase.FirebaseFirestoreSingleton;
import com.example.storeclothes.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserRepository {
    private static UserRepository instance;
    private final FirebaseFirestore firestore;
    private static final String IMAGEKIT_UPLOAD_URL = "https://upload.imagekit.io/api/v1/files/upload";
    private static final String IMAGEKIT_PUBLIC_KEY = "public_xxoDxA/rmR/ItuATyOU5k5c/slE=";
    private static final String IMAGEKIT_PRIVATE_KEY = "private_IwsI+3E5LZq5gDM7d8wYhq0VIhQ=";

    private UserRepository() {
        this.firestore = FirebaseFirestoreSingleton.getInstance();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public LiveData<User> getUserById(String userId) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        
        if (userId == null || userId.isEmpty()) {
            userLiveData.setValue(null);
            return userLiveData;
        }
        
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.setValue(user);
                    } else {
                        userLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu cần
                    userLiveData.setValue(null);
                });
                
        return userLiveData;
    }
    
    public LiveData<Boolean> updateUser(User user) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        
        firestore.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(true))
                .addOnFailureListener(e -> resultLiveData.setValue(false));
                
        return resultLiveData;
    }

    public LiveData<String> uploadImageToImageKit(Uri imageUri, Context context) {
        MutableLiveData<String> uploadResult = new MutableLiveData<>();
        new Thread(() -> {
            try {
                // Resize ảnh
                byte[] imageBytes = resizeImage(context, imageUri, 800, 800);

                OkHttpClient client = new OkHttpClient();

                RequestBody fileBody = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "avatar_" + System.currentTimeMillis() + ".jpg", fileBody)
                        .addFormDataPart("fileName", "avatar_" + System.currentTimeMillis() + ".jpg")
                        .addFormDataPart("publicKey", IMAGEKIT_PUBLIC_KEY)
                        .build();

                String credential = Base64.encodeToString((IMAGEKIT_PRIVATE_KEY + ":").getBytes(), Base64.NO_WRAP);

                Request request = new Request.Builder()
                        .url(IMAGEKIT_UPLOAD_URL)
                        .addHeader("Authorization", "Basic " + credential)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    uploadResult.postValue(null);
                    return;
                }
                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                if (json.has("url")) {
                    uploadResult.postValue(json.getString("url"));
                } else {
                    uploadResult.postValue(null);
                }
            } catch (Exception e) {
                uploadResult.postValue(null);
            }
        }).start();
        return uploadResult;
    }

    private byte[] resizeImage(Context context, Uri uri, int maxWidth, int maxHeight) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        Bitmap original = BitmapFactory.decodeStream(inputStream);

        int width = original.getWidth();
        int height = original.getHeight();

        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);

        int scaledWidth = Math.round(width * scale);
        int scaledHeight = Math.round(height * scale);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        return outputStream.toByteArray();
    }
}