package com.example.storeclothes.ui.Fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Category;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Fragment.Category.CategoryActivity;
import com.example.storeclothes.ui.Fragment.Cart.CartActivity;
import com.example.storeclothes.ui.Fragment.Category.CategoryAdapter;
import com.example.storeclothes.ui.Fragment.Order.OrderActivity;
import com.example.storeclothes.ui.Fragment.Product.ProductAdapter;
import com.example.storeclothes.ui.Fragment.Product.ProductDetailActivity;
import com.example.storeclothes.ui.Fragment.Profile.InformationActivity;
import com.example.storeclothes.ui.Fragment.Profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting,tvSeeAllNewIn,tvSeeAllTopSelling;
    private String userUid;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewCategory, recyclerViewProductTopSelling, recyclerViewProductNewIn;
    private ImageView avatarImageView;
    private ShapeableImageView ibtnCart;
    private EditText edtSearch;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initViews();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();

    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvSeeAllNewIn = findViewById(R.id.tvSeeAllNewIn);
        tvSeeAllTopSelling = findViewById(R.id.tvSeeAllTopSelling);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewProductNewIn = findViewById(R.id.recyclerViewProductsNewIn);
        recyclerViewProductTopSelling = findViewById(R.id.recyclerViewProductsTopSelling);
        avatarImageView = findViewById(R.id.ibtnAvata);
        ibtnCart = findViewById(R.id.ibtnCart);
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.setFocusable(false);
    }
    private void setupRecyclerView(){
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewProductNewIn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewProductTopSelling.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void setupClickListeners() {
        avatarImageView.setOnClickListener(v -> openActivity(InformationActivity.class));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_notification) {
                openActivity(HomeActivity.class);
            } else if (itemId == R.id.nav_order) {
                openActivity(OrderActivity.class);
            } else if (itemId == R.id.nav_profile) {
                openActivity(ProfileActivity.class);
            }
            return true;
        });
        ibtnCart.setOnClickListener(v -> openActivity(CartActivity.class));
        edtSearch.setOnClickListener(v -> openActivity(SearchActivity.class));
        tvSeeAllNewIn.setOnClickListener(v -> openActivity(SearchActivity.class));
        tvSeeAllTopSelling.setOnClickListener(v -> openActivity(SearchActivity.class));
    }
    private void observeViewModel() {
        // Quan sát thông tin người dùng
        viewModel.getUserById(userUid).observe(this, user -> {
            if (user != null) {
                tvGreeting.setText("Hi, " + user.getFirstName());
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Glide.with(HomeActivity.this)
                            .load(user.getAvatarUrl())
                            .placeholder(R.drawable.ic_loading)
                            .into(avatarImageView);
                }
            } else {
                tvGreeting.setText("Hi, Guest");
                Toast.makeText(HomeActivity.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát danh sách danh mục
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                setupCategoryAdapter(categories);
            } else {
                Toast.makeText(HomeActivity.this, "Không thể lấy danh sách danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát danh sách sản phẩm
        viewModel.getAllProducts().observe(this, products -> {
            if (products != null) {
                setupProductAdapter(products);
            } else {
                Toast.makeText(HomeActivity.this, "Không thể lấy danh sách sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoryAdapter(List<Category> categories) {
        CategoryAdapter categoryAdapter = new CategoryAdapter(HomeActivity.this, categories, category -> {
            Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
            intent.putExtra("category_id", category.getCategoryId());
            intent.putExtra("category_name", category.getName());
            startActivity(intent);
        });
        recyclerViewCategory.setAdapter(categoryAdapter);
    }

    private void setupProductAdapter(List<Product> products) {
        ProductAdapter productAdapter = new ProductAdapter(HomeActivity.this, products, product -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("user_id", userUid);
            intent.putExtra("product_id", product.getProductId());
            startActivity(intent);
        });
        recyclerViewProductNewIn.setAdapter(productAdapter);
        recyclerViewProductTopSelling.setAdapter(productAdapter);
    }
    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
    @Override
    protected void onResume() {
        super.onResume();
        observeViewModel();
    }
}