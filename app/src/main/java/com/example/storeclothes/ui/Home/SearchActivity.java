package com.example.storeclothes.ui.Home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Product.ProductAdapter;
import com.example.storeclothes.ui.Product.ProductDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewProduct;
    private TextView tvNoProducts;
    private LinearLayout filterButtonsContainer;
    private EditText edtSearch;
    private MaterialButton chipPrice, chipSort, chipGender;
    private FloatingActionButton fabBack;
    private float selectedMinPrice = 0f;
    private float selectedMaxPrice = 1000f;
    private ProductAdapter productAdapter;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = FirebaseFirestore.getInstance();
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadAllProductsFromFirebase();
    }

    private void initViews() {
        fabBack = findViewById(R.id.fabBack);
        edtSearch = findViewById(R.id.edtSearch);
        tvNoProducts = findViewById(R.id.tvNoProducts);
        chipPrice = findViewById(R.id.chipPrice);
        chipSort = findViewById(R.id.chipSort);
        recyclerViewProduct = findViewById(R.id.recyclerView);
        filterButtonsContainer = findViewById(R.id.filterButtonsContainer);
    }

    private void setupRecyclerView() {
        recyclerViewProduct.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupClickListeners() {
        fabBack.setOnClickListener(view -> finish());

        chipSort.setOnClickListener(view -> showSortBottomSheet());
        chipPrice.setOnClickListener(view -> showPriceBottomSheet());

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
                String keyword = s.toString().trim();
                if (keyword.length() > 0) {
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_clear, 0);
                } else {
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
                }
                searchWithFilters(keyword);
            }
        });
    }

    private void showSortBottomSheet() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_sort_filter);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogSlideAnimation);
        }
        MaterialButton btnApply = dialog.findViewById(R.id.btnApplySort);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnApply.setOnClickListener(v -> dialog.dismiss());
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showPriceBottomSheet() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_price_filter);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogSlideAnimation);
        }

        TextView tvPriceRange = dialog.findViewById(R.id.tvPriceRange);
        RangeSlider priceRangeSlider = dialog.findViewById(R.id.priceRangeSlider);
        MaterialButton btnApplyPrice = dialog.findViewById(R.id.btnApplyPrice);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);

        priceRangeSlider.setValues(0f, 1000f);

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            float min = values.get(0);
            float max = values.get(1);
            tvPriceRange.setText("$" + (int) min + " - $" + (int) max);
        });

        btnApplyPrice.setOnClickListener(v -> {
            List<Float> values = priceRangeSlider.getValues();
            selectedMinPrice = values.get(0);
            selectedMaxPrice = values.get(1);
            chipPrice.setText("$" + (int) selectedMinPrice + " - $" + (int) selectedMaxPrice);
            chipPrice.setTextColor(getResources().getColor(R.color.white));
            chipPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_white, 0);
            chipPrice.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_main));
            dialog.dismiss();
            String currentKeyword = edtSearch.getText().toString().trim();
            searchWithFilters(currentKeyword);
        });

        btnCancel.setOnClickListener(v -> {
            chipPrice.setText("Price");
            chipPrice.setTextColor(getResources().getColor(R.color.color_text));
            chipPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            chipPrice.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.background_gray));
            dialog.dismiss();
            selectedMinPrice = 0f;
            selectedMaxPrice = 1000f;
            String currentKeyword = edtSearch.getText().toString().trim();
            searchWithFilters(currentKeyword);
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadAllProductsFromFirebase() {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (var doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        product.setProductId(doc.getId()); // nếu ID không có sẵn trong object
                        allProducts.add(product);
                    }
                    String currentKeyword = edtSearch.getText().toString().trim();
                    searchWithFilters(currentKeyword);
                })
                .addOnFailureListener(e -> {
                    tvNoProducts.setText("Lỗi khi tải sản phẩm");
                    tvNoProducts.setVisibility(View.VISIBLE);
                });
    }

    private void searchWithFilters(String keyword) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                try {
                    double price = p.getPrice();
                    if (price >= selectedMinPrice && price <= selectedMaxPrice) {
                        filtered.add(p);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        if (filtered.isEmpty()) {
            filterButtonsContainer.setVisibility(View.GONE);
            recyclerViewProduct.setVisibility(View.GONE);
            tvNoProducts.setVisibility(View.VISIBLE);
        } else {
            filterButtonsContainer.setVisibility(View.VISIBLE);
            recyclerViewProduct.setVisibility(View.VISIBLE);
            tvNoProducts.setVisibility(View.GONE);


            productAdapter = new ProductAdapter(this, filtered, product -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("product_id", product.getProductId());
                startActivity(intent);});
            recyclerViewProduct.setAdapter(productAdapter);
        }
    }
}
