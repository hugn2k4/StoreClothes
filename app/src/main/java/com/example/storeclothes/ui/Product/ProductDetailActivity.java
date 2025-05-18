package com.example.storeclothes.ui.Product;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.data.service.ProductService;
import com.example.storeclothes.data.service.WishlistService;
import com.example.storeclothes.ui.Home.HomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private String productId,userUid ;
    private boolean isFavorite = false;
    private FloatingActionButton fabFavorite, fabBack;
    private TextView tvNameProduct, tvPriceProduct, tvDescription;
    private RecyclerView recyclerViewImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productId = getIntent().getStringExtra("product_id");
        userUid = getIntent().getStringExtra("user_id");
        if (productId == null) {
            finish();
            return;
        }
        initViews();
        loadProductDetails(productId);
        setupClickListeners();
    }
    private void initViews() {
        fabFavorite = findViewById(R.id.fabFavorite);
        fabBack = findViewById(R.id.fabBack);
        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvPriceProduct = findViewById(R.id.tvPriceProduct);
        tvDescription = findViewById(R.id.tvDescription);
        recyclerViewImage = findViewById(R.id.recyclerViewImage);
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void setupClickListeners() {
        fabBack.setOnClickListener(v -> finish());
    }
    private void loadProductDetails(String productId) {
        ProductService.getInstance().getProductById(productId, new ProductService.OnProductDetailListener() {
            @Override
            public void onSuccess(Product product) {
                tvNameProduct.setText(product.getName());
                tvPriceProduct.setText("$" + String.format("%,d", product.getPrice().intValue()));
                tvDescription.setText(product.getDescription());
                recyclerViewImage.setAdapter(new ImageAdapter(ProductDetailActivity.this, product.getImages()));
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Không thể lấy sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        String wishlistId = userUid + "_" + productId;

        WishlistService.getInstance().getWishlistByUser(userUid, new WishlistService.OnWishlistListListener() {
            @Override
            public void onSuccess(List<Wishlist> wishlists) {
                for (Wishlist w : wishlists) {
                    if (w.getProductId().equals(productId)) {
                        isFavorite = true;
                        break;
                    }
                }
                updateFavoriteIcon();
                setupFavoriteClickListener(wishlistId, productId);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Xử lý lỗi nếu cần
            }
        });

    }
    private void updateFavoriteIcon() {
        fabFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );
    }
    private void setupFavoriteClickListener(String wishlistId, String productId) {
        fabFavorite.setOnClickListener(v -> {
            if (isFavorite) {
                WishlistService.getInstance().removeFromWishlist(wishlistId, new WishlistService.OnWishlistActionListener() {
                    @Override
                    public void onSuccess() {
                        isFavorite = false;
                        updateFavoriteIcon();
                        Toast.makeText(ProductDetailActivity.this, "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Wishlist wishlist = new Wishlist(wishlistId, userUid, productId);
                WishlistService.getInstance().addToWishlist(wishlist, new WishlistService.OnWishlistActionListener() {
                    @Override
                    public void onSuccess() {
                        isFavorite = true;
                        updateFavoriteIcon();
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}