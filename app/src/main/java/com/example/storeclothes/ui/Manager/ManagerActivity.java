package com.example.storeclothes.ui.Manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.service.ProductService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private FloatingActionButton fabAddProduct;
    private ProgressBar progressBar;
    private ProductManagerAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadProducts();
    }

    private void initViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductManagerAdapter(this, productList, new ProductManagerAdapter.OnProductActionListener() {
            @Override
            public void onEditClick(Product product) {
                showEditProductDialog(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                showDeleteConfirmationDialog(product);
            }

            @Override
            public void onItemClick(Product product) {
                // Hiển thị chi tiết sản phẩm nếu cần
                Toast.makeText(ManagerActivity.this, "Đã chọn: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    private void loadProducts() {
        showLoading(true);
        ProductService.getInstance().getProductListFromFirebase(new ProductService.OnProductListListener() {
            @Override
            public void onSuccess(List<Product> products) {
                showLoading(false);
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showAddProductDialog() {
        // Tạo sản phẩm mẫu với ID mới
        String newProductId = UUID.randomUUID().toString();
        Product newProduct = new Product(newProductId, "Sản phẩm mới", "Mô tả sản phẩm", 100000.0, "", new ArrayList<>());
        
        // Thêm sản phẩm vào Firestore
        showLoading(true);
        ProductService.getInstance().addProduct(newProduct, new ProductService.OnProductAddListener() {
            @Override
            public void onSuccess() {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Đã thêm sản phẩm mới", Toast.LENGTH_SHORT).show();
                // Tải lại danh sách sản phẩm
                loadProducts();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Lỗi khi thêm sản phẩm: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setView(view);

        // Khởi tạo các view trong dialog
        TextInputEditText edtProductName = view.findViewById(R.id.edtProductName);
        TextInputEditText edtProductPrice = view.findViewById(R.id.edtProductPrice);
        TextInputEditText edtProductDescription = view.findViewById(R.id.edtProductDescription);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Đặt giá trị hiện tại của sản phẩm
        edtProductName.setText(product.getName());
        if (product.getPrice() != null) {
            edtProductPrice.setText(String.valueOf(product.getPrice()));
        }
        edtProductDescription.setText(product.getDescription());

        AlertDialog dialog = builder.create();

        // Xử lý sự kiện nút Hủy
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Xử lý sự kiện nút Lưu
        btnSave.setOnClickListener(v -> {
            // Lấy thông tin từ form
            String name = edtProductName.getText().toString().trim();
            String priceStr = edtProductPrice.getText().toString().trim();
            String description = edtProductDescription.getText().toString().trim();

            // Kiểm tra dữ liệu
            if (name.isEmpty()) {
                edtProductName.setError("Vui lòng nhập tên sản phẩm");
                return;
            }

            if (priceStr.isEmpty()) {
                edtProductPrice.setError("Vui lòng nhập giá sản phẩm");
                return;
            }

            // Cập nhật thông tin sản phẩm
            Double price = Double.parseDouble(priceStr);
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);

            // Lưu thay đổi vào Firestore
            updateProduct(product);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateProduct(Product product) {
        showLoading(true);
        ProductService.getInstance().updateProduct(product, new ProductService.OnProductUpdateListener() {
            @Override
            public void onSuccess() {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Đã cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                // Cập nhật lại danh sách sản phẩm
                loadProducts();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Lỗi khi cập nhật sản phẩm: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Product product) {
        showLoading(true);
        ProductService.getInstance().deleteProduct(product.getProductId(), new ProductService.OnProductDeleteListener() {
            @Override
            public void onSuccess() {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Đã xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                // Xóa sản phẩm khỏi danh sách và cập nhật adapter
                productList.remove(product);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Lỗi khi xóa sản phẩm: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts(); // Tải lại danh sách sản phẩm khi quay lại màn hình
    }
}