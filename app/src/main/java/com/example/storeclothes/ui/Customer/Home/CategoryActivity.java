package com.example.storeclothes.ui.Customer.Home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.ui.Customer.Adapter.ProductAdapter;
import com.example.storeclothes.ui.Customer.Product.ProductDetailActivity;
import com.example.storeclothes.ui.Customer.ViewModel.CategoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CategoryActivity extends ComponentActivity {

    private CategoryViewModel viewModel;
    private ProductAdapter productAdapter;

    private RecyclerView recyclerView;
    private FloatingActionButton fabBack;
    private EditText edtSearch;
    private TextView tvCategoryName, tvNoProducts;
    private LinearLayout filterButtonsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String categoryId = getIntent().getStringExtra("category_id");
        String categoryName = getIntent().getStringExtra("category_name");

        // ViewModel
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        initViews();
        tvCategoryName.setText(categoryName);

        setupRecyclerView();
        setupClickListeners();

        // Set categoryId để load dữ liệu
        viewModel.setCategoryId(categoryId);

        // Observe filteredProducts để cập nhật UI
        viewModel.getFilteredProducts().observe(this, products -> {
            if (products == null || products.isEmpty()) {
                filterButtonsContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                tvNoProducts.setVisibility(View.VISIBLE);
            } else {
                filterButtonsContainer.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                tvNoProducts.setVisibility(View.GONE);

                productAdapter.updateList(products);
            }
        });
    }

    private void initViews() {
        fabBack = findViewById(R.id.fabBack);
        edtSearch = findViewById(R.id.edtSearch);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvNoProducts = findViewById(R.id.tvNoProducts);
        filterButtonsContainer = findViewById(R.id.filterButtonsContainer);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
            intent.putExtra("user_id", userId);
            intent.putExtra("product_id", product.getProductId());
            startActivity(intent);
        });
        recyclerView.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        fabBack.setOnClickListener(view -> finish());

        edtSearch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[2].getBounds().width())) {
                    edtSearch.setText("");
                    return true;
                }
            }
            return false;
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setKeyword(s.toString().trim());
            }
        });
    }
}
