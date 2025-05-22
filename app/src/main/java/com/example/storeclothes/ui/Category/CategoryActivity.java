package com.example.storeclothes.ui.Category;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Product.ProductAdapter;
import com.example.storeclothes.ui.Product.ProductDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabBack;
    private EditText edtSearch;
    private TextView tvCategoryName, tvNoProducts;
    private LinearLayout filterButtonsContainer;
    private String categoryId;

    private FirebaseFirestore db;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryId = getIntent().getStringExtra("category_id");
        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadDataFromFirebase();
    }

    private void initViews() {
        fabBack = findViewById(R.id.fabBack);
        edtSearch = findViewById(R.id.edtSearch);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvNoProducts = findViewById(R.id.tvNoProducts);
        filterButtonsContainer = findViewById(R.id.filterButtonsContainer);
        recyclerView = findViewById(R.id.recyclerView);

        String categoryName = getIntent().getStringExtra("category_name");
        tvCategoryName.setText(categoryName);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadDataFromFirebase() {
        if (categoryId == null || categoryId.isEmpty()) return;
        db.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allProducts.clear();
                    for (var doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        product.setProductId(doc.getId());
                        allProducts.add(product);
                    }
                    searchWithKeyword(edtSearch.getText().toString().trim());
                })
                .addOnFailureListener(e -> {
                    tvNoProducts.setText("Lỗi khi tải sản phẩm");
                    tvNoProducts.setVisibility(View.VISIBLE);
                });
    }

    private void searchWithKeyword(String keyword) {
        List<Product> filteredList = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(p);
            }
        }

        if (filteredList.isEmpty()) {
            filterButtonsContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            tvNoProducts.setVisibility(View.VISIBLE);
        } else {
            filterButtonsContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            tvNoProducts.setVisibility(View.GONE);

            productAdapter = new ProductAdapter(this, filteredList, product -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("product_id", product.getProductId());
                startActivity(intent);});
            recyclerView.setAdapter(productAdapter);

        }
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
                String keyword = s.toString().trim();
                if (keyword.length() > 0) {
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_clear, 0);
                } else {
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
                }
                searchWithKeyword(keyword);
            }
        });
    }
}
