package com.example.storeclothes.ui.Admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.repository.CategoryRepository;
import com.example.storeclothes.data.repository.ProductRepository;
import com.example.storeclothes.ui.Admin.manager.BaseManager;
import com.example.storeclothes.ui.Admin.manager.ProductManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductFormActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etProductName, etProductDescription, etProductPrice;
    private Spinner spinnerCategory;
    private ImageView ivProductImage;
    private Button btnSave, btnAddImage;

    private BaseManager<Product> productManager;
    private CategoryRepository categoryRepository;
    private List<String> categoryIds = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private Uri imageUri;
    private String productId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Bằng đoạn mã này:
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    
        // Khởi tạo các thành phần UI
        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductPrice = findViewById(R.id.etProductPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnSave = findViewById(R.id.btnSave);
        btnAddImage = findViewById(R.id.btnAddImage);

        // Khởi tạo repositories và managers
        productManager = new ProductManager();
        categoryRepository = CategoryRepository.getInstance();

        // Thiết lập toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Kiểm tra xem đang ở chế độ thêm mới hay chỉnh sửa
        if (getIntent().hasExtra("productId")) {
            isEditMode = true;
            productId = getIntent().getStringExtra("productId");
            getSupportActionBar().setTitle("Chỉnh sửa sản phẩm");
            loadProductData();
        } else {
            isEditMode = false;
            productId = UUID.randomUUID().toString();
            getSupportActionBar().setTitle("Thêm sản phẩm mới");
        }

        // Tải danh sách danh mục
        loadCategories();

        // Thiết lập sự kiện
        btnAddImage.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void loadCategories() {
        categoryRepository.getCategories().observe(this, categories -> {
            categoryIds.clear();
            categoryNames.clear();

            for (var category : categories) {
                categoryIds.add(category.getCategoryId());
                categoryNames.add(category.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            // Nếu đang ở chế độ chỉnh sửa, chọn danh mục hiện tại
            if (isEditMode && getIntent().hasExtra("categoryId")) {
                String categoryId = getIntent().getStringExtra("categoryId");
                int position = categoryIds.indexOf(categoryId);
                if (position >= 0) {
                    spinnerCategory.setSelection(position);
                }
            }
        });
    }

    private void loadProductData() {
        productManager.getById(productId).observe(this, product -> {
            if (product != null) {
                etProductName.setText(product.getName());
                etProductDescription.setText(product.getDescription());
                etProductPrice.setText(String.valueOf(product.getPrice()));
                imageUrls = product.getImages() != null ? product.getImages() : new ArrayList<>();

                // Hiển thị ảnh đầu tiên nếu có
                if (!imageUrls.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrls.get(0))
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(ivProductImage);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivProductImage);
        }
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy categoryId từ spinner
        int categoryPosition = spinnerCategory.getSelectedItemPosition();
        if (categoryPosition < 0 || categoryPosition >= categoryIds.size()) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return;
        }
        String categoryId = categoryIds.get(categoryPosition);

        // Nếu có ảnh mới, tải lên Firebase Storage
        if (imageUri != null) {
            uploadImage(name, description, price, categoryId);
        } else {
            // Nếu không có ảnh mới, lưu sản phẩm với ảnh hiện tại
            saveProductToFirestore(name, description, price, categoryId, imageUrls);
        }
    }

    private void uploadImage(String name, String description, double price, String categoryId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_images");
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        List<String> newImageUrls = new ArrayList<>(imageUrls);
                        newImageUrls.add(imageUrl);
                        saveProductToFirestore(name, description, price, categoryId, newImageUrls);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProductToFirestore(String name, String description, double price, String categoryId, List<String> images) {
        Product product = new Product.Builder()
                .productId(productId)
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .images(images)
                .build();

        productManager.add(product).observe(this, success -> {
            if (success) {
                Toast.makeText(this, isEditMode ? "Sản phẩm đã được cập nhật" : "Sản phẩm đã được thêm", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}