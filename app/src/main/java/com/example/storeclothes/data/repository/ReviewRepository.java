package com.example.storeclothes.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Review;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    private final FirebaseFirestore db;
    private static ReviewRepository instance;
    public ReviewRepository() {
        db = FirebaseFirestore.getInstance();
    }
    public static ReviewRepository getInstance() {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }
    public Task<Void> addReviewAndMarkOrderReviewed(Review review, Order order, OrderItem orderItem) {
        return db.collection("reviews")
                .add(review)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Sau khi thêm review thành công, cập nhật trạng thái reviewed cho order item
                    return db.collection("orders").document(order.getOrderId()).get();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Order updatedOrder = task.getResult().toObject(Order.class);
                    if (updatedOrder != null && updatedOrder.getItems() != null) {
                        for (OrderItem item : updatedOrder.getItems()) {
                            if (item.getCartItem() != null &&
                                    item.getCartItem().getProductId().equals(orderItem.getCartItem().getProductId())) {
                                item.setReviewed(true);
                                break;
                            }
                        }
                        return db.collection("orders").document(order.getOrderId()).set(updatedOrder);
                    }
                    return null;
                });
    }
    public LiveData<List<Review>> getReviewDetailsByProductId(String productId) {
        MutableLiveData<List<Review>> reviewLiveData = new MutableLiveData<>();
        db.collection("reviews")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Review> reviewList = new ArrayList<>();
                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();

                    // Lưu các review gốc (để build sau)
                    List<Review> rawReviews = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Review review = doc.toObject(Review.class);
                        rawReviews.add(review);

                        // Lấy userId để truy vấn user
                        Task<DocumentSnapshot> userTask = db.collection("users")
                                .document(review.getUserId())
                                .get();
                        userTasks.add(userTask);
                    }

                    Tasks.whenAllSuccess(userTasks).addOnSuccessListener(users -> {
                        for (int i = 0; i < rawReviews.size(); i++) {
                            Review r = rawReviews.get(i);
                            DocumentSnapshot userDoc = (DocumentSnapshot) users.get(i);

                            String userName = userDoc.getString("firstName");
                            String avatarUrl = userDoc.getString("avatarUrl");

                            // Dùng Builder để tạo Review mới với tên và avatar user
                            Review reviewWithUserInfo = new Review.Builder()
                                    .setReviewId(r.getReviewId())
                                    .setUserId(r.getUserId())
                                    .setProductId(r.getProductId())
                                    .setRating(r.getRating())
                                    .setComment(r.getComment())
                                    .setDate(r.getDate())
                                    .setUserName(userName)
                                    .setAvatarUrl(avatarUrl)
                                    .build();

                            reviewList.add(reviewWithUserInfo);
                        }
                        reviewLiveData.setValue(reviewList);
                    });
                })
                .addOnFailureListener(e -> {
                    reviewLiveData.setValue(new ArrayList<>());
                });

        return reviewLiveData;
    }

}
