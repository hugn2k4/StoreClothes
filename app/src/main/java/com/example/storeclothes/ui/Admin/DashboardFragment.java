package com.example.storeclothes.ui.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.ui.Admin.adapter.TopProductAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment {
    private RecyclerView recyclerViewTopProducts;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private TextView tvTotalUsers;
    private TextView tvTotalRevenue;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các thành phần UI
        recyclerViewTopProducts = view.findViewById(R.id.recyclerViewTopProducts);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoData = view.findViewById(R.id.tvNoData);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);

        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();

        // Thiết lập RecyclerView
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải tất cả dữ liệu dashboard
        loadDashboardData();
    }

    private void loadDashboardData() {
        loadTotalUsers();
        loadTopProducts();
        loadTotalRevenue();
    }

    private void loadTotalUsers() {
        firestore.collection("users")
                .whereEqualTo("role", "CUSTOMER")  // Lọc theo role = CUSTOMER
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalUsers = queryDocumentSnapshots.size();
                    if (tvTotalUsers != null) {
                        tvTotalUsers.setText(String.valueOf(totalUsers));
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvTotalUsers != null) {
                        tvTotalUsers.setText("0");
                    }
                });
    }
    private void loadTotalRevenue() {
        firestore.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalRevenue = 0.0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        if (order != null) {
                            totalRevenue += order.getTotalAmount();
                        }
                    }

                    // Format số tiền theo định dạng VND
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
                    String formattedRevenue = formatter.format(totalRevenue);
                    tvTotalRevenue.setText(formattedRevenue);
                })
                .addOnFailureListener(e -> {
                    tvTotalRevenue.setText("$0");
                });
    }

    private void loadTopProducts() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        // Map để lưu trữ số lượng sản phẩm đã bán theo productId
        Map<String, Integer> productQuantityMap = new HashMap<>();

        // Lấy tất cả đơn hàng từ Firestore
        firestore.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        if (order != null && order.getItems() != null) {
                            for (OrderItem item : order.getItems()) {
                                CartItem cartItem = item.getCartItem();
                                if (cartItem != null) {
                                    String productId = cartItem.getProductId();
                                    int quantity = cartItem.getQuantity();

                                    // Cập nhật số lượng trong map
                                    productQuantityMap.put(productId,
                                            productQuantityMap.getOrDefault(productId, 0) + quantity);
                                }
                            }
                        }
                    }

                    // Lấy danh sách productId đã sắp xếp theo số lượng bán
                    List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productQuantityMap.entrySet());
                    Collections.sort(sortedEntries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));


                    // Lấy tối đa 10 sản phẩm bán chạy nhất
                    List<String> topProductIds = new ArrayList<>();
                    int count = 0;
                    for (Map.Entry<String, Integer> entry : sortedEntries) {
                        if (count < 3) {
                            topProductIds.add(entry.getKey());
                            count++;
                        } else {
                            break;
                        }
                    }

                    if (topProductIds.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Lấy thông tin chi tiết của các sản phẩm top
                    firestore.collection("products")
                            .whereIn("productId", topProductIds)
                            .get()
                            .addOnSuccessListener(productSnapshots -> {
                                List<Product> topProducts = new ArrayList<>();
                                Map<String, Integer> quantityMap = new HashMap<>();

                                // Lấy thông tin sản phẩm và số lượng đã bán
                                for (QueryDocumentSnapshot doc : productSnapshots) {
                                    Product product = doc.toObject(Product.class);
                                    if (product != null) {
                                        topProducts.add(product);
                                        quantityMap.put(product.getProductId(),
                                                productQuantityMap.get(product.getProductId()));
                                    }
                                }

                                // Sắp xếp lại danh sách sản phẩm theo số lượng bán
                                Collections.sort(topProducts, (p1, p2) -> {
                                    Integer q1 = quantityMap.get(p1.getProductId());
                                    Integer q2 = quantityMap.get(p2.getProductId());
                                    return q2.compareTo(q1);
                                });

                                // Hiển thị danh sách sản phẩm
                                progressBar.setVisibility(View.GONE);
                                if (topProducts.isEmpty()) {
                                    tvNoData.setVisibility(View.VISIBLE);
                                } else {
                                    TopProductAdapter adapter = new TopProductAdapter(topProducts, quantityMap);
                                    recyclerViewTopProducts.setAdapter(adapter);
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                });
    }
}