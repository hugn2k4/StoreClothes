package com.example.storeclothes.ui.Manager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private BarChart chartRevenue;
    private PieChart chartTopProducts;
    private RecyclerView recyclerViewTopProducts;
    private RecyclerView recyclerViewRecentUsers;
    private TextView tvTotalRevenue;
    private TextView tvTotalOrders;
    private TextView tvTotalUsers;
    private ProgressBar progressBar;

    private StatisticsService statisticsService;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        statisticsService = StatisticsService.getInstance();

        initViews(view);
        loadStatistics();
    }

    private void initViews(View view) {
        chartRevenue = view.findViewById(R.id.chartRevenue);
        chartTopProducts = view.findViewById(R.id.chartTopProducts);
        recyclerViewTopProducts = view.findViewById(R.id.recyclerViewTopProducts);
        recyclerViewRecentUsers = view.findViewById(R.id.recyclerViewRecentUsers);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        progressBar = view.findViewById(R.id.progressBar);

        // Thiết lập RecyclerView
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRecentUsers.setLayoutManager(new LinearLayoutManager(getContext()));
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
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu doanh thu: " + errorMessage, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Chưa có dữ liệu sản phẩm bán chạy", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị biểu đồ tròn cho top sản phẩm
                    setupTopProductsChart(topProducts);
                    
                    // Hiển thị danh sách sản phẩm bán chạy
                    TopProductAdapter adapter = new TopProductAdapter(getContext(), topProducts);
                    recyclerViewTopProducts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
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
                    UserAdapter adapter = new UserAdapter(getContext(), recentUsers);
                    recyclerViewRecentUsers.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Không có dữ liệu người dùng mới", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }
    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}