package com.example.storeclothes.ui.Fragment.Wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Adapter.ProductAdapter;
import com.example.storeclothes.ui.Fragment.Product.ProductDetailActivity;
import com.example.storeclothes.ui.ViewModel.WishlistViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {

    private WishlistViewModel viewModel;
    private RecyclerView recyclerViewProduct;
    private FloatingActionButton buttonBack;
    private TextView tvRemoveAll;
    private String userUid;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        userUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (userUid == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        initViews();
        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        viewModel.loadWishlistProducts(userUid);
    }

    private void initViews() {
        recyclerViewProduct = findViewById(R.id.recyclerView);
        buttonBack = findViewById(R.id.fabBack);
        tvRemoveAll = findViewById(R.id.tvRemoveAll);
    }

    private void setupRecyclerView() {
        recyclerViewProduct.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getProductId());
            intent.putExtra("user_id", userUid);
            startActivity(intent);
        });
        recyclerViewProduct.setAdapter(productAdapter);
    }

    private void setupObservers() {
        viewModel.getWishlistProducts().observe(this, products -> {
            if (products != null) {
                productAdapter.updateList(products);
                if (products.isEmpty()) {
                    Toast.makeText(this, "Danh sách yêu thích trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getRemoveAllSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đã xoá toàn bộ sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                productAdapter.updateList(new ArrayList<>());
            }
        });

        viewModel.getRemoveAllError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupClickListeners() {
        buttonBack.setOnClickListener(view -> finish());
        tvRemoveAll.setOnClickListener(view -> viewModel.removeAllWishlistItems(userUid));
    }
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadWishlistProducts(userUid);
    }
}