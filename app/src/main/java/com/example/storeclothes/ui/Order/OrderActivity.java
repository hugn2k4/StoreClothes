package com.example.storeclothes.ui.Order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private FloatingActionButton fabBack;
    private RecyclerView recyclerView;
    private OrderItemAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<Order> orders = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    private TextView tvEmptyOrders;
    private TextView tvRemoveAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initViews();
        setOnClickListeners();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadOrders(user.getUid());
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            tvEmptyOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setText("Vui lòng đăng nhập để xem đơn hàng");
            tvRemoveAll.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        tvEmptyOrders.setVisibility(View.GONE);
        tvRemoveAll = findViewById(R.id.tvRemoveAll);
        tvRemoveAll.setVisibility(View.GONE);

        fabBack = findViewById(R.id.fabBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListeners() {
        fabBack.setOnClickListener(view -> finish());
        tvRemoveAll.setOnClickListener(view -> {
            // Logic xóa tất cả đơn hàng (tùy chỉnh theo yêu cầu)
        });
    }

    private void loadOrders(String userId) {
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orders.clear();
                    List<String> productIds = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Order order = doc.toObject(Order.class);
                            if (order != null) {
                                order.setOrderId(doc.getId());
                                orders.add(order);
                                if (order.getItems() != null) {
                                    for (OrderItem item : order.getItems()) {
                                        if (item.getCartItem() != null) {
                                            productIds.add(item.getCartItem().getProductId());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Lỗi deserialize được xử lý im lặng
                        }
                    }
                    if (orders.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                        tvRemoveAll.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvEmptyOrders.setVisibility(View.GONE);
                        tvRemoveAll.setVisibility(View.VISIBLE);
                        adapter.setOrders(orders);
                        if (!productIds.isEmpty()) {
                            loadProducts(productIds);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                    tvRemoveAll.setVisibility(View.GONE);
                });
    }

    private void loadProducts(List<String> productIds) {
        db.collection("products")
                .whereIn("productId", productIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productMap.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Product product = doc.toObject(Product.class);
                            if (product != null && product.getProductId() != null) {
                                productMap.put(product.getProductId(), product);
                            }
                        } catch (Exception e) {
                            // Lỗi deserialize được xử lý im lặng
                        }
                    }
                    adapter.setProductMap(productMap);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}