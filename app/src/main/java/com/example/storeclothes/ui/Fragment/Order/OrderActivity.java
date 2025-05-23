package com.example.storeclothes.ui.Fragment.Order;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.ui.ViewModel.OrderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends ComponentActivity {

    private FloatingActionButton fabBack;
    private RecyclerView recyclerView;
    private OrderItemAdapter adapter;
    private TextView tvEmptyOrders, tvRemoveAll;
    private OrderViewModel viewModel;
    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initViews();
        setupListeners();

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            showNoUserUI();
        } else {
            viewModel.setUserId(user.getUid());
            observeData();
        }
    }

    private void initViews() {
        fabBack = findViewById(R.id.fabBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter(this);
        recyclerView.setAdapter(adapter);

        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        tvRemoveAll = findViewById(R.id.tvRemoveAll);

        tvEmptyOrders.setVisibility(View.GONE);
        tvRemoveAll.setVisibility(View.GONE);
    }

    private void setupListeners() {
        fabBack.setOnClickListener(v -> finish());

        tvRemoveAll.setOnClickListener(v -> {
            // TODO: Xử lý xóa tất cả đơn hàng nếu cần (có thể gọi ViewModel để xóa)
        });
    }

    private void showNoUserUI() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyOrders.setVisibility(View.VISIBLE);
        tvEmptyOrders.setText("Vui lòng đăng nhập để xem đơn hàng");
        tvRemoveAll.setVisibility(View.GONE);
        Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
    }

    private void observeData() {
        viewModel.orders.observe(this, orderList -> {
            orders.clear();
            if (orderList == null || orderList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setText("Bạn chưa có đơn hàng nào");
                tvRemoveAll.setVisibility(View.GONE);
                adapter.setOrders(new ArrayList<>());
                return;
            }
            orders.addAll(orderList);
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyOrders.setVisibility(View.GONE);
            tvRemoveAll.setVisibility(View.VISIBLE);
            adapter.setOrders(orderList);
        });

        viewModel.productMap.observe(this, productMap -> {
            // Cập nhật map sản phẩm cho adapter để hiển thị đúng tên, giá,...
            adapter.setProductMap(productMap);
        });
    }
}
