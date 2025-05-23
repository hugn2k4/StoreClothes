package com.example.storeclothes.ui.Manager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.service.StatisticsService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private BarChart chartRevenue;
    private PieChart chartTopProducts;
    private RecyclerView recyclerViewTopProducts;
    private RecyclerView recyclerViewRecentUsers;
    private TextView tvTotalRevenue;
    private TextView tvTotalOrders;
    private TextView tvTotalUsers;
    private ProgressBar progressBar;
    private FloatingActionButton fabBack;

    private StatisticsService statisticsService;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statisticsService = StatisticsService.getInstance();

        initViews();
        setupClickListeners();
        loadStatistics();
    }

    private void initViews() {
        chartRevenue = findViewById(R.id.chartRevenue);
        chartTopProducts = findViewById(R.id.chartTopProducts);
        recyclerViewTopProducts = findViewById(R.id.recyclerViewTopProducts);
        recyclerViewRecentUsers = findViewById(R.id.recyclerViewRecentUsers);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        progressBar = findViewById(R.id.progressBar);
        fabBack = findViewById(R.id.fabBack);

        // Thiết lập RecyclerView
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecentUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        fabBack.setOnClickListener(v -> finish());
    }

    private void loadStatistics() {
        showLoading(true);
        loadRevenueStatistics();
        loadTopProductsStatistics();
        loadUserStatistics();
    }

    private void loadRevenueStatistics() {
        // Lấy dữ liệu doanh thu trong 7 ngày gần nhất
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, -6); // 7 ngày (ngày hiện tại + 6 ngày trước)
        Date startDate = calendar.getTime();

        statisticsService.getRevenueByPeriod(startDate, endDate, new StatisticsService.OnRevenueStatisticsListener() {
            @Override
            public void onSuccess(double totalRevenue, int orderCount, Map<String, Double> dailyRevenue) {
                // Hiển thị tổng doanh thu và số đơn hàng
                tvTotalRevenue.setText(currencyFormat.format(totalRevenue));
                tvTotalOrders.setText(String.valueOf(orderCount));

                // Hiển thị biểu đồ doanh thu theo ngày
                setupRevenueChart(startDate, endDate, dailyRevenue);
                showLoading(false);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(StatisticsActivity.this, "Lỗi khi tải dữ liệu doanh thu: " + errorMessage, Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    private void setupRevenueChart(Date startDate, Date endDate, Map<String, Double> dailyRevenue) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        // Tạo danh sách ngày từ startDate đến endDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        int index = 0;
        while (!calendar.after(endCalendar)) {
            Date currentDate = calendar.getTime();
            String dateKey = currentDate.toString();
            String label = dateFormat.format(currentDate);
            
            // Lấy doanh thu của ngày hiện tại
            double revenue = dailyRevenue.getOrDefault(dateKey, 0.0);
            entries.add(new BarEntry(index, (float) revenue));
            labels.add(label);
            
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu theo ngày");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        chartRevenue.setData(barData);
        chartRevenue.getDescription().setEnabled(false);
        chartRevenue.setDrawGridBackground(false);
        chartRevenue.setDrawBarShadow(false);
        chartRevenue.setDrawValueAboveBar(true);
        chartRevenue.setPinchZoom(false);
        chartRevenue.setDrawGridBackground(false);

        // Thiết lập trục X
        XAxis xAxis = chartRevenue.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setDrawGridLines(false);

        // Thiết lập trục Y bên trái
        YAxis leftAxis = chartRevenue.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        // Ẩn trục Y bên phải
        chartRevenue.getAxisRight().setEnabled(false);
        chartRevenue.getLegend().setEnabled(false);

        chartRevenue.animateY(1000);
        chartRevenue.invalidate();
    }

    private void loadTopProductsStatistics() {
        statisticsService.getTopSellingProducts(5, new StatisticsService.OnTopProductsListener() {
            @Override
            public void onSuccess(List<Product> topProducts) {
                // Hiển thị danh sách sản phẩm bán chạy
                if (topProducts.isEmpty()) {
                    // Hiển thị thông báo không có dữ liệu
                    Toast.makeText(StatisticsActivity.this, "Chưa có dữ liệu sản phẩm bán chạy", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị biểu đồ tròn cho top sản phẩm
                    setupTopProductsChart(topProducts);
                    
                    // Hiển thị danh sách sản phẩm bán chạy
                    TopProductAdapter adapter = new TopProductAdapter(StatisticsActivity.this, topProducts);
                    recyclerViewTopProducts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(StatisticsActivity.this, "Lỗi khi tải dữ liệu sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTopProductsChart(List<Product> topProducts) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        
        // Thêm dữ liệu vào biểu đồ tròn
        for (int i = 0; i < Math.min(topProducts.size(), 5); i++) {
            Product product = topProducts.get(i);
            entries.add(new PieEntry(product.getPrice().floatValue(), product.getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Top sản phẩm bán chạy");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        chartTopProducts.setData(pieData);
        chartTopProducts.getDescription().setEnabled(false);
        chartTopProducts.setDrawHoleEnabled(true);
        chartTopProducts.setHoleColor(Color.WHITE);
        chartTopProducts.setTransparentCircleRadius(61f);
        chartTopProducts.setEntryLabelColor(Color.WHITE);
        chartTopProducts.setEntryLabelTextSize(12f);
        chartTopProducts.setDrawEntryLabels(true);

        chartTopProducts.animateY(1000);
        chartTopProducts.invalidate();
    }

    private void loadUserStatistics() {
        statisticsService.getUserStatistics(new StatisticsService.OnUserStatisticsListener() {
            @Override
            public void onSuccess(int totalUsers, List<User> recentUsers) {
                // Hiển thị tổng số người dùng
                tvTotalUsers.setText(String.valueOf(totalUsers));
                
                // Hiển thị danh sách người dùng mới nhất
                if (recentUsers != null && !recentUsers.isEmpty()) {
                    UserAdapter adapter = new UserAdapter(StatisticsActivity.this, recentUsers);
                    recyclerViewRecentUsers.setAdapter(adapter);
                } else {
                    Toast.makeText(StatisticsActivity.this, "Không có dữ liệu người dùng mới", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(StatisticsActivity.this, "Lỗi khi tải dữ liệu người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}