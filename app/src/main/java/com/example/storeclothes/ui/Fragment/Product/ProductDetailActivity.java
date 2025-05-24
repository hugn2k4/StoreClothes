package com.example.storeclothes.ui.Fragment.Product;

import android.app.Dialog;
import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.ColorItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.ui.Adapter.ColorAdapter;
import com.example.storeclothes.ui.Adapter.ImageAdapter;
import com.example.storeclothes.ui.Adapter.ProductAdapter;
import com.example.storeclothes.ui.Adapter.SizeAdapter;
import com.example.storeclothes.ui.ViewModel.ProductViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductViewModel viewModel;
    private String productId, userUid, categoryId;
    private boolean isFavorite = false;
    private TextView tvName, tvPrice, tvDescription, tvSize, tvQuantity;
    private RecyclerView rvImages, rvRelatedProducts;
    private MaterialCardView cardAddToCart, cardColor, cardSize;
    private View colorIndicator;
    private FloatingActionButton fabFavorite, fabBack;
    private MaterialButton btnIncrease, btnDecrease;
    private int quantity = 1;
    private String selectedColor = "White";
    private String selectedSize = "L";

    private final List<ColorItem> colorList = Arrays.asList(
            new ColorItem("White", R.color.white_custom),
            new ColorItem("Red", R.color.red_custom),
            new ColorItem("Blue", R.color.blue_custom),
            new ColorItem("Green", R.color.green_custom),
            new ColorItem("Yellow", R.color.yellow_custom)
    );
    private final List<String> sizeList = Arrays.asList("S", "M", "L", "XL", "XXL");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Get Intent data
        productId = getIntent().getStringExtra("product_id");
        userUid = getIntent().getStringExtra("user_id");
        // userUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Uncomment for Firebase Auth
        if (productId == null || userUid == null) {
            Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Initialize UI and load data
        initializeViews();
        setupObservers();
        setupClickListeners();
        viewModel.loadProductDetails(productId);
        viewModel.checkWishlistStatus(userUid, productId)
                .observe(this, isFavorite -> {
                    if (isFavorite != null) {
                        this.isFavorite = isFavorite;
                        updateFavoriteIcon();
                    }
                });
    }

    private void initializeViews() {
        fabFavorite = findViewById(R.id.fabFavorite);
        fabBack = findViewById(R.id.fabBack);
        tvName = findViewById(R.id.tvNameProduct);
        tvPrice = findViewById(R.id.tvPriceProduct);
        tvDescription = findViewById(R.id.tvDescription);
        tvSize = findViewById(R.id.tvSelectedSize);
        tvQuantity = findViewById(R.id.tvQuantity);
        cardAddToCart = findViewById(R.id.cardButton);
        cardColor = findViewById(R.id.cardButtonColor);
        cardSize = findViewById(R.id.cardButtonSize);
        colorIndicator = findViewById(R.id.colorPreview);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        rvImages = findViewById(R.id.recyclerViewImage);
        rvRelatedProducts = findViewById(R.id.recyclerViewReview);

        // Setup RecyclerViews
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRelatedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set initial values
        tvSize.setText(selectedSize);
        tvQuantity.setText(String.valueOf(quantity));
        updateColorIndicator();
    }

    private void setupObservers() {
        // Observe product details
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                tvName.setText(product.getName());
                tvPrice.setText(String.format("$%.2f", product.getPrice()));
                tvDescription.setText(product.getDescription());
                rvImages.setAdapter(new ImageAdapter(this, product.getImages()));
                categoryId = product.getCategoryId();
                // Load related products
                viewModel.getProductsByCategory(categoryId).observe(this, relatedProducts -> {
                    if (relatedProducts != null) {
                        List<Product> filteredProducts = relatedProducts.stream()
                                .filter(p -> !p.getProductId().equals(productId))
                                .collect(Collectors.toList());
                        ProductAdapter adapter = new ProductAdapter(this, filteredProducts, p -> {
                            Intent intent = new Intent(this, ProductDetailActivity.class);
                            intent.putExtra("product_id", p.getProductId());
                            intent.putExtra("user_id", userUid);
                            startActivity(intent);
                        });
                        rvRelatedProducts.setAdapter(adapter);
                    }
                });
            }
        });

        // Observe product errors
        viewModel.getProductError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe wishlist status
        viewModel.getWishlistStatus().observe(this, status -> {
            if (status != null) {
                isFavorite = status;
                updateFavoriteIcon();
            }
        });

        // Observe wishlist actions
        viewModel.getWishlistActionSuccess().observe(this, success -> {
            if (success) {
                isFavorite = !isFavorite;
                updateFavoriteIcon();
                Toast.makeText(this, isFavorite ? "Đã thêm vào yêu thích" : "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getWishlistActionError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe cart actions
        viewModel.getAddToCartSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getAddToCartError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        fabBack.setOnClickListener(v -> finish());
        fabFavorite.setOnClickListener(v -> handleFavoriteClick());
        cardAddToCart.setOnClickListener(v -> handleAddToCart());
        cardColor.setOnClickListener(v -> showColorDialog());
        cardSize.setOnClickListener(v -> showSizeDialog());
        btnIncrease.setOnClickListener(v -> updateQuantity(true));
        btnDecrease.setOnClickListener(v -> updateQuantity(false));
    }

    private void updateFavoriteIcon() {
        fabFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
    }

    private void handleFavoriteClick() {
        String wishlistId = userUid + "_" + productId;
        if (isFavorite) {
            viewModel.removeFromWishlist(wishlistId).observe(this, success -> {
                if (success) {
                    isFavorite = false;
                    updateFavoriteIcon();
                    Toast.makeText(this, "Đã xoá khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Xoá yêu thích thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Wishlist wishlist = new Wishlist(wishlistId, userUid, productId);
            viewModel.addToWishlist(wishlist).observe(this, success -> {
                if (success != null && success) {
                    isFavorite = true;
                    updateFavoriteIcon();
                    Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm yêu thích thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleAddToCart() {
        CartItem item = new CartItem.Builder()
                .setProductId(productId)
                .setQuantity(quantity)
                .setSize(selectedSize)
                .setColor(selectedColor)
                .build();
        viewModel.addToCart(userUid, item);
    }

    private void updateQuantity(boolean increase) {
        if (increase && quantity < 10) {
            quantity++;
        } else if (!increase && quantity > 1) {
            quantity--;
        }
        tvQuantity.setText(String.valueOf(quantity));
    }

    private void updateColorIndicator() {
        for (ColorItem item : colorList) {
            if (item.getName().equals(selectedColor)) {
                int colorInt = ContextCompat.getColor(this, item.getColorInt());
                GradientDrawable bgShape = (GradientDrawable) colorIndicator.getBackground();
                bgShape.setColor(colorInt);
                break;
            }
        }
    }

    private void showColorDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_select_color);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogSlideAnimation);
        }

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerColorList);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ColorAdapter(this, colorList, selectedColor, colorName -> {
            selectedColor = colorName;
            updateColorIndicator();
            dialog.dismiss();
        }));

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSizeDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_select_size);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogSlideAnimation);
        }

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerSizeList);
        ImageView btnClose = dialog.findViewById(R.id.btnCloseSize);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SizeAdapter(this, sizeList, selectedSize, sizeName -> {
            selectedSize = sizeName;
            tvSize.setText(sizeName);
            dialog.dismiss();
        }));

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}