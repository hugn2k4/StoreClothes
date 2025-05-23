package com.example.storeclothes.data.service;

import android.util.Log;

import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsService {
    private static StatisticsService instance;
    private final FirebaseFirestore firestore;

    private StatisticsService() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static StatisticsService getInstance() {
        if (instance == null) {
            instance = new StatisticsService();
        }
        return instance;
    }

    // Lấy tổng doanh thu theo khoảng thời gian
    public void getRevenueByPeriod(Date startDate, Date endDate, OnRevenueStatisticsListener listener) {
        firestore.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", startDate)
                .whereLessThanOrEqualTo("orderDate", endDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalRevenue = 0;
                        List<Order> orders = new ArrayList<>();
                        Map<String, Double> dailyRevenue = new HashMap<>();

                        Calendar calendar = Calendar.getInstance();
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            orders.add(order);
                            
                            // Tính tổng doanh thu
                            if (order.getTotalAmount() != null) {
                                totalRevenue += order.getTotalAmount();
                            }
                            
                            // Tính doanh thu theo ngày
                            if (order.getOrderDate() != null) {
                                calendar.setTime(order.getOrderDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                
                                String dateKey = calendar.getTime().toString();
                                Double currentRevenue = dailyRevenue.getOrDefault(dateKey, 0.0);
                                dailyRevenue.put(dateKey, currentRevenue + (order.getTotalAmount() != null ? order.getTotalAmount() : 0));
                            }
                        }
                        
                        listener.onSuccess(totalRevenue, orders.size(), dailyRevenue);
                    } else {
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    // Lấy sản phẩm bán chạy nhất
    public void getTopSellingProducts(int limit, OnTopProductsListener listener) {
        // Giả sử có bảng OrderItem lưu thông tin chi tiết đơn hàng
        firestore.collection("orderItems")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Kiểm tra nếu không có dữ liệu
                        if (task.getResult().isEmpty()) {
                            Log.d("StatisticsService", "Không có dữ liệu sản phẩm bán chạy");
                            listener.onSuccess(new ArrayList<>());
                            return;
                        }
                        
                        Map<String, Integer> productSales = new HashMap<>();
                        
                        // Đếm số lượng bán của mỗi sản phẩm
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String productId = document.getString("productId");
                                Integer quantity = document.getLong("quantity") != null ? document.getLong("quantity").intValue() : 0;
                                
                                if (productId != null) {
                                    Integer currentCount = productSales.getOrDefault(productId, 0);
                                    productSales.put(productId, currentCount + quantity);
                                }
                            } catch (Exception e) {
                                Log.e("StatisticsService", "Lỗi khi xử lý document: " + e.getMessage());
                            }
                        }
                        
                        // Kiểm tra nếu không có sản phẩm nào được bán
                        if (productSales.isEmpty()) {
                            listener.onSuccess(new ArrayList<>());
                            return;
                        }
                        
                        // Sắp xếp và lấy top sản phẩm bán chạy
                        List<Map.Entry<String, Integer>> sortedProducts = new ArrayList<>(productSales.entrySet());
                        sortedProducts.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                        
                        // Lấy danh sách ID sản phẩm bán chạy nhất
                        List<String> topProductIds = new ArrayList<>();
                        int count = 0;
                        for (Map.Entry<String, Integer> entry : sortedProducts) {
                            if (count < limit) {
                                topProductIds.add(entry.getKey());
                                count++;
                            } else {
                                break;
                            }
                        }
                        
                        // Lấy thông tin chi tiết của các sản phẩm bán chạy
                        if (!topProductIds.isEmpty()) {
                            getProductsByIds(topProductIds, listener);
                        } else {
                            listener.onSuccess(new ArrayList<>());
                        }
                    } else {
                        Log.e("StatisticsService", "Lỗi khi lấy dữ liệu sản phẩm bán chạy: " + 
                              (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    // Lấy thông tin chi tiết của các sản phẩm theo danh sách ID
    private void getProductsByIds(List<String> productIds, OnTopProductsListener listener) {
        // Kiểm tra nếu danh sách productIds rỗng
        if (productIds.isEmpty()) {
            listener.onSuccess(new ArrayList<>());
            return;
        }
        
        List<Product> products = new ArrayList<>();
        final int[] completedQueries = {0};
        
        for (String productId : productIds) {
            firestore.collection("products")
                    .document(productId)
                    .get()
                    .addOnCompleteListener(task -> {
                        completedQueries[0]++;
                        
                        if (task.isSuccessful() && task.getResult().exists()) {
                            Product product = task.getResult().toObject(Product.class);
                            if (product != null) {
                                products.add(product);
                            }
                        }
                        
                        // Kiểm tra nếu đã hoàn thành tất cả các truy vấn
                        if (completedQueries[0] == productIds.size()) {
                            listener.onSuccess(products);
                        }
                    });
        }
    }

    // Lấy thống kê người dùng
    public void getUserStatistics(OnUserStatisticsListener listener) {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalUsers = task.getResult().size();
                        List<User> recentUsers = new ArrayList<>();
                        
                        // Lấy danh sách người dùng mới nhất
                        firestore.collection("users")
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(5)
                                .get()
                                .addOnCompleteListener(recentTask -> {
                                    if (recentTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : recentTask.getResult()) {
                                            User user = document.toObject(User.class);
                                            recentUsers.add(user);
                                        }
                                    }
                                    listener.onSuccess(totalUsers, recentUsers);
                                });
                    } else {
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    // Interface cho callback thống kê doanh thu
    public interface OnRevenueStatisticsListener {
        void onSuccess(double totalRevenue, int orderCount, Map<String, Double> dailyRevenue);
        void onFailure(String errorMessage);
    }

    // Interface cho callback thống kê sản phẩm bán chạy
    public interface OnTopProductsListener {
        void onSuccess(List<Product> topProducts);
        void onFailure(String errorMessage);
    }

    // Interface cho callback thống kê người dùng
    public interface OnUserStatisticsListener {
        void onSuccess(int totalUsers, List<User> recentUsers);
        void onFailure(String errorMessage);
    }
}