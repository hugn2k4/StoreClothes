package com.example.storeclothes.data.repository;

import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Review;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewRepository {
    private FirebaseFirestore db;

    public ReviewRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Thêm đánh giá vào Firestore
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
}
