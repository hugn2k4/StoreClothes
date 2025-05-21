package com.example.storeclothes.ui.Product;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.ColorItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.data.service.CartRepository;
import com.example.storeclothes.data.service.ProductService;
import com.example.storeclothes.data.service.WishlistService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private String productId,userUid ;
    private boolean isFavorite = false;
    private FloatingActionButton fabFavorite, fabBack;
    private TextView tvNameProduct, tvPriceProduct, tvDescription,tvSelectedSize, tvQuantity;
    private RecyclerView recyclerViewImage;
    private MaterialCardView cardButton, cardColor, cardSize;
    private View colorIndicator;
    private MaterialButton btnIncrease, btnDecrease;
    private int quantity = 1;
    private String color = "White";
    private String size = "L";

    private List<ColorItem> colorList = Arrays.asList(
            new ColorItem("White", R.color.white_custom),
            new ColorItem("Red", R.color.red_custom),
            new ColorItem("Blue", R.color.blue_custom),
            new ColorItem("Green", R.color.green_custom),
            new ColorItem("Yellow", R.color.yellow_custom)
    );
    private List<String> sizeList = Arrays.asList("S", "M", "L", "XL", "XXL");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productId = getIntent().getStringExtra("product_id");
        userUid = getIntent().getStringExtra("user_id");
//        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        tvSelectedSize = findViewById(R.id.tvSelectedSize);
        tvQuantity = findViewById(R.id.tvQuantity);
        cardButton = findViewById(R.id.cardButton);
        cardColor = findViewById(R.id.cardButtonColor);
        cardSize = findViewById(R.id.cardButtonSize);
        colorIndicator = findViewById(R.id.colorPreview);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        recyclerViewImage = findViewById(R.id.recyclerViewImage);
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void setupClickListeners() {
        fabBack.setOnClickListener(v -> finish());
        cardColor.setOnClickListener(v -> openColorDialog(color));
        cardSize.setOnClickListener(v -> openSizeDialog(size));
        btnIncrease.setOnClickListener(v -> increaseQuantity());
        btnDecrease.setOnClickListener(v -> decreaseQuantity());
        cardButton.setOnClickListener(v->handleAddToCart());
    }
    private void loadProductDetails(String productId) {
        ProductService.getInstance().getProductById(productId, new ProductService.OnProductDetailListener() {
            @Override
            public void onSuccess(Product product) {
                tvNameProduct.setText(product.getName());
                tvPriceProduct.setText("$" + String.format("%.2f", product.getPrice()));
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
        tvSelectedSize.setText(size);
        for (ColorItem item : colorList) {
            if (item.getName().equals(color)) {
                int colorInt = ContextCompat.getColor(this, item.getColorInt());
                GradientDrawable bgShape = (GradientDrawable) colorIndicator.getBackground();
                bgShape.setColor(colorInt);
                break;
            }
        }
        tvQuantity.setText(String.valueOf(quantity));
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
    private void openColorDialog(String selectedColor) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_select_color);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogSlideAnimation);
        }

        RecyclerView recyclerColorList = dialog.findViewById(R.id.recyclerColorList);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);

        ColorAdapter adapter = new ColorAdapter(this, colorList, selectedColor, colorName -> {
            for (ColorItem item : colorList) {
                if (item.getName().equals(colorName)) {
                    int colorInt = ContextCompat.getColor(this, item.getColorInt());
                    // Cập nhật màu cho indicator
                    GradientDrawable bgShape = (GradientDrawable) colorIndicator.getBackground();
                    bgShape.setColor(colorInt);
                    color = colorName;
                    break;
                }
            }
            dialog.dismiss();
        });
        recyclerColorList.setLayoutManager(new LinearLayoutManager(this));
        recyclerColorList.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void openSizeDialog(String selectedSize) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_select_size);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogSlideAnimation);
        }

        RecyclerView recyclerSizeList = dialog.findViewById(R.id.recyclerSizeList);
        ImageView btnClose = dialog.findViewById(R.id.btnCloseSize);

        SizeAdapter adapter = new SizeAdapter(this,sizeList, selectedSize, sizeName -> {
            size = sizeName;
            tvSelectedSize.setText(sizeName);
            dialog.dismiss();
        });
        recyclerSizeList.setLayoutManager(new LinearLayoutManager(this));
        recyclerSizeList.setAdapter(adapter);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void increaseQuantity() {
        if (quantity < 10) {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        }
    }
    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            tvQuantity.setText(String.valueOf(quantity));
        }
    }

    private void handleAddToCart() {
        CartRepository cartRepo = new CartRepository();

        CartItem newItem = new CartItem.Builder()
                .setProductId(productId)
                .setQuantity(quantity)
                .setSize(size)
                .setColor(color)
                .build();

        cartRepo.addToCart(newItem, new CartRepository.OnCartUpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}