package com.example.storeclothes.ui.Wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.data.service.ProductService;
import com.example.storeclothes.data.service.WishlistService;
import com.example.storeclothes.ui.Product.ProductAdapter;
import com.example.storeclothes.ui.Product.ProductDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProduct;
    private FloatingActionButton buttonBack;
    private TextView tvRemoveAll;
    private String userUid;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initViews();
        setupRecyclerView();
        setupClickListeners();
    }
    private void initViews() {
        recyclerViewProduct = findViewById(R.id.recyclerView);
        buttonBack= findViewById(R.id.fabBack);
        tvRemoveAll = findViewById(R.id.tvRemoveAll);
    }
    private void setupRecyclerView(){
        recyclerViewProduct.setLayoutManager(new GridLayoutManager(this, 2));
    }
    private void setupClickListeners() {
        buttonBack.setOnClickListener(view -> finish());
        tvRemoveAll.setOnClickListener(view -> removeAll());
    }
    private void loadProductWishlist() {
        productList.clear();
        WishlistService.getInstance().getWishlistByUser(userUid, new WishlistService.OnWishlistListListener() {
            @Override
            public void onSuccess(List<Wishlist> wishlists) {
                if (wishlists.isEmpty()) {
                    setupProductAdapter();
                    return;
                }
                final int[] loadedCount = {0};
                for (Wishlist wishlist : wishlists) {
                    ProductService.getInstance().getProductById(wishlist.getProductId(), new ProductService.OnProductDetailListener() {
                        @Override
                        public void onSuccess(Product product) {
                            productList.add(product);
                            checkAllProductsLoaded(wishlists.size(), ++loadedCount[0]);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            checkAllProductsLoaded(wishlists.size(), ++loadedCount[0]);
                        }
                    });
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(WishlistActivity.this, "Lỗi khi lấy danh sách yêu thích: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkAllProductsLoaded(int total, int loaded) {
        if (loaded == total) {
            setupProductAdapter();
        }
    }
    private void setupProductAdapter() {
        productAdapter = new ProductAdapter(WishlistActivity.this, productList, product -> {
            Intent intent = new Intent(WishlistActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getProductId());
            intent.putExtra("user_id", userUid);
            startActivity(intent);
        });
        recyclerViewProduct.setAdapter(productAdapter);
    }
    private void removeAll() {
        WishlistService.getInstance().removeAllFromWishlist(userUid, new WishlistService.OnWishlistActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WishlistActivity.this, "Đã xoá toàn bộ sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                productList.clear();
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(WishlistActivity.this, "Lỗi khi xoá: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadProductWishlist();
    }
}