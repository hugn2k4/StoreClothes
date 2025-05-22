package com.example.storeclothes.ui.Order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Review;
import com.example.storeclothes.ui.Product.ProductDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItemEntry> orderItemEntries = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    private FirebaseFirestore db;
    private Context context;
    public static class OrderItemEntry {
        public Order order;
        public OrderItem orderItem;

        public OrderItemEntry(Order order, OrderItem orderItem) {
            this.order = order;
            this.orderItem = orderItem;
        }
    }

    public OrderItemAdapter(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void setOrders(List<Order> orders) {
        orderItemEntries.clear();
        for (Order order : orders) {
            if (order.getItems() != null) {
                for (OrderItem orderItem : order.getItems()) {
                    orderItemEntries.add(new OrderItemEntry(order, orderItem));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setProductMap(Map<String, Product> map) {
        this.productMap = map;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItemEntry entry = orderItemEntries.get(position);
        Product product = entry.orderItem.getCartItem() != null ? productMap.get(entry.orderItem.getCartItem().getProductId()) : null;
        holder.bind(entry.order, entry.orderItem, product);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getProductId());
            intent.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderItemEntries.size();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvSize, tvColor, tvPrice, tvStatus;
        ImageView ivProductImage;
        RatingBar ratingBar;
        EditText etReview;
        MaterialButton btnSubmitReview;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSize = itemView.findViewById(R.id.tvSizeValue);
            tvColor = itemView.findViewById(R.id.tvColorValue);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivProductImage = itemView.findViewById(R.id.imageProduct);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            etReview = itemView.findViewById(R.id.etReview);
            btnSubmitReview = itemView.findViewById(R.id.btnSubmitReview);
        }

        public void bind(Order order, OrderItem orderItem, Product product) {
            if (orderItem != null && orderItem.getCartItem() != null && product != null) {
                // Hiển thị thông tin từ Product
                tvProductName.setText(product.getName());
                tvPrice.setText(String.format("$%.2f", product.getPrice() * orderItem.getCartItem().getQuantity()));

                // Hiển thị thông tin từ CartItem
                tvQuantity.setText(String.format("%d item", orderItem.getCartItem().getQuantity()));
                tvSize.setText(orderItem.getCartItem().getSize() != null ? orderItem.getCartItem().getSize() : "-");
                tvColor.setText(orderItem.getCartItem().getColor() != null ? orderItem.getCartItem().getColor() : "-");

                // Hiển thị ảnh sản phẩm
                List<String> images = product.getImages();
                if (images != null && !images.isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(images.get(0))
                            .placeholder(R.drawable.ic_loading)
                            .into(ivProductImage);
                } else {
                    ivProductImage.setImageResource(R.drawable.ic_loading);
                }
            } else {
                tvProductName.setText("Unknown product");
                tvPrice.setText("$0.00");
                tvQuantity.setText("0 item");
                tvSize.setText("-");
                tvColor.setText("-");
                ivProductImage.setImageResource(R.drawable.ic_loading);
            }

            // Hiển thị trạng thái từ Order
            tvStatus.setText(order.getStatus() != null ? order.getStatus() : "Unknown");

            // Xử lý đánh giá
            if (orderItem != null && !orderItem.isReviewed()) {
                ratingBar.setVisibility(View.VISIBLE);
                etReview.setVisibility(View.VISIBLE);
                btnSubmitReview.setVisibility(View.VISIBLE);

                btnSubmitReview.setOnClickListener(v -> {
                    float rating = ratingBar.getRating();
                    String reviewText = etReview.getText().toString().trim();

                    if (rating == 0) {
                        Toast.makeText(itemView.getContext(), "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (reviewText.isEmpty()) {
                        Toast.makeText(itemView.getContext(), "Vui lòng nhập đánh giá", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tạo đối tượng Review
                    Review review = new Review();
                    review.setProductId(orderItem.getCartItem().getProductId());
                    review.setUserId(order.getUserId());
                    review.setRating(rating);
                    review.setComment(reviewText);
                    review.setDate(new Date());

                    // Lưu vào Firestore
                    db.collection("reviews")
                            .add(review)
                            .addOnSuccessListener(documentReference -> {
                                // Cập nhật trạng thái reviewed
                                db.collection("orders")
                                        .document(order.getOrderId())
                                        .get()
                                        .addOnSuccessListener(doc -> {
                                            Order updatedOrder = doc.toObject(Order.class);
                                            if (updatedOrder != null && updatedOrder.getItems() != null) {
                                                for (OrderItem item : updatedOrder.getItems()) {
                                                    if (item.getCartItem() != null && item.getCartItem().getProductId().equals(orderItem.getCartItem().getProductId())) {
                                                        item.setReviewed(true);
                                                        break;
                                                    }
                                                }
                                                db.collection("orders")
                                                        .document(order.getOrderId())
                                                        .set(updatedOrder)
                                                        .addOnSuccessListener(aVoid -> {
                                                            orderItem.setReviewed(true);
                                                            ratingBar.setVisibility(View.GONE);
                                                            etReview.setVisibility(View.GONE);
                                                            btnSubmitReview.setVisibility(View.GONE);
                                                            Toast.makeText(itemView.getContext(), "Đánh giá đã được gửi", Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(itemView.getContext(), "Lỗi khi gửi đánh giá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
            } else {
                ratingBar.setVisibility(View.GONE);
                etReview.setVisibility(View.GONE);
                btnSubmitReview.setVisibility(View.GONE);
            }
        }
    }
}