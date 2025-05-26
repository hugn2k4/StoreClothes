package com.example.storeclothes.ui.Customer.Product;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.ColorItem;
import com.example.storeclothes.data.model.Review;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.ui.Customer.Adapter.ColorAdapter;
import com.example.storeclothes.ui.Customer.Adapter.ImageAdapter;
import com.example.storeclothes.ui.Customer.Adapter.SizeAdapter;
import com.example.storeclothes.ui.Customer.Adapter.ReviewAdapter;
import com.example.storeclothes.ui.Customer.Wishlist.WishlistManager;
import com.example.storeclothes.ui.Customer.Wishlist.WishlistObserver;
import com.example.storeclothes.ui.Customer.ViewModel.ProductViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity implements WishlistObserver{

    private ProductViewModel viewModel;
    private String productId, userUid;
    private boolean isFavorite = false;
    private TextView tvName, tvPrice,tvPriceProduct, tvDescription, tvSize, tvQuantity,txtRating, txtNumberReview;
    private RecyclerView rvImages, recyclerViewReview;
    private MaterialCardView cardAddToCart, cardColor, cardSize;
    private View colorIndicator;
    private FloatingActionButton fabFavorite, fabBack;
    private MaterialButton btnIncrease, btnDecrease;
    private int quantity = 1;
    private String selectedColor = "White";
    private String selectedSize = "L";
    private WishlistManager wishlistManager;

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

        productId = getIntent().getStringExtra("product_id");
        userUid = getIntent().getStringExtra("user_id");
        // userUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Uncomment for Firebase Auth
        if (productId == null || userUid == null) {
            showToast("Thông tin không hợp lệ");
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        // Observe
        wishlistManager = new WishlistManager();
        wishlistManager.registerObserver(this);
//        wishlistManager.registerObserver(new WishlistObserver() {
//            @Override
//            public void onWishlistStatusChanged(boolean isFavorite) {
//                Log.e("WishlistObserver", "onWishlistStatusChanged: " + isFavorite);
//            }
//        });

        initializeViews();
        setupObservers();
        setupClickListeners();
        viewModel.loadProductDetails(productId);
        viewModel.checkWishlistStatus(userUid, productId)
                .observe(this, isFavorite -> {
                    if (isFavorite != null) {
                        wishlistManager.setFavorite(isFavorite);
                    }
                });
        loadProductReviews();
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void loadProductReviews() {
        viewModel.getReviewList().observe(this, reviews -> {
            recyclerViewReview.setAdapter(new ReviewAdapter(this, reviews));
            int totalReviews = reviews.size();
            float totalRating = 0;
            for (Review review : reviews) {
                totalRating += review.getRating();
            }
            float averageRating = totalReviews > 0 ? totalRating / totalReviews : 5.0f;
            txtRating.setText(String.format("%.1f Ratings", averageRating));
            txtNumberReview.setText(String.format("%d Reviews", totalReviews));
        });
    }
    private void initializeViews() {
        fabFavorite = findViewById(R.id.fabFavorite);
        fabBack = findViewById(R.id.fabBack);
        tvName = findViewById(R.id.tvNameProduct);
        tvPrice = findViewById(R.id.tvPrice);
        tvPriceProduct = findViewById(R.id.tvPriceProduct);
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
        recyclerViewReview = findViewById(R.id.recyclerViewReview);
        txtRating = findViewById(R.id.txtRating);
        txtNumberReview = findViewById(R.id.txtNumberReview);
        // Setup RecyclerViews
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
                tvPriceProduct.setText(String.format("$%.2f", product.getPrice()));
                tvDescription.setText(product.getDescription());
                rvImages.setAdapter(new ImageAdapter(this, product.getImages()));
                viewModel.loadReviews(productId);
            }
        });

        // Observe product errors
        viewModel.getProductError().observe(this, error -> {
            if (error != null) {
                showToast(error);
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
                showToast(isFavorite ? "Đã thêm vào yêu thích" : "Đã xoá khỏi yêu thích");
            }
        });
        viewModel.getWishlistActionError().observe(this, error -> {
            if (error != null) {
                showToast(error);
            }
        });

        // Observe cart actions
        viewModel.getAddToCartSuccess().observe(this, success -> {
            if (success) {
                showToast("Đã thêm vào giỏ hàng!");
            }
        });
        viewModel.getAddToCartError().observe(this, error -> {
            if (error != null) {
                showToast(error);
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
        fabFavorite.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction(() -> fabFavorite.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                ).start();
    }
    private void handleFavoriteClick() {
        String wishlistId = userUid + "_" + productId;
        if (isFavorite) {
            viewModel.removeFromWishlist(wishlistId).observe(this, success -> {
                if (success) {
//                    isFavorite = false;
//                    updateFavoriteIcon();
                    wishlistManager.setFavorite(false);
                    showToast("Đã xoá khỏi yêu thích");
                } else {
                    showToast("Xoá yêu thích thất bại");
                }
            });
        } else {
            Wishlist wishlist = new Wishlist.Builder()
                    .setWishlistId(wishlistId)
                    .setUserId(userUid)
                    .setProductId(productId)
                    .build();
            viewModel.addToWishlist(wishlist).observe(this, success -> {
                if (success != null && success) {
//                    isFavorite = true;
//                    updateFavoriteIcon();
                    wishlistManager.setFavorite(true);
                    showToast("Đã thêm vào yêu thích");
                } else {
                    showToast("Thêm yêu thích thất bại");
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
        btnIncrease.setEnabled(quantity < 10);
        btnDecrease.setEnabled(quantity > 1);
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
            updateSelectedColor(colorName);
            quantity = 1;
            tvQuantity.setText(String.valueOf(quantity));
            btnIncrease.setEnabled(true);
            btnDecrease.setEnabled(false);
            dialog.dismiss();
        }));

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void updateSelectedColor(String colorName) {
        selectedColor = colorName;
        updateColorIndicator();
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


    @Override
    public void onWishlistStatusChanged(boolean isFavorite) {
        this.isFavorite = isFavorite;
        updateFavoriteIcon();
        // Có thể show Toast hoặc cập nhật UI khác nếu cần
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wishlistManager.removeObserver(this);
    }
}