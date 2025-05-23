package com.example.storeclothes.ui.Manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.storeclothes.data.firebase.Authentication;
import com.example.storeclothes.ui.Authentication.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManagerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fabAddProduct;
    private ProgressBar progressBar;
    private Uri selectedImageUri;
    private String currentProductImagePath;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        ImageView imgProductPhoto = findViewById(R.id.imgProductPhoto);
                        if (imgProductPhoto != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .centerCrop()
                                    .into(imgProductPhoto);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        
        initViews();
        setupNavigationDrawer();
        setupClickListeners();
        checkPermissions();
        
        // Mặc định hiển thị fragment quản lý sản phẩm khi khởi động
        if (savedInstanceState == null) {
            loadFragment(new ProductsFragment());
            navigationView.setCheckedItem(R.id.nav_products);
            setTitle("Quản lý sản phẩm");
        }
    }
    
    private void checkPermissions() {
        // Kiểm tra quyền truy cập bộ nhớ ngoài
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1001);
            }
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Android 6-12 sử dụng READ_EXTERNAL_STORAGE
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập thư viện ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh từ thư viện", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        progressBar = findViewById(R.id.progressBar);
        
        // Ẩn FAB ban đầu, chỉ hiển thị khi ở fragment sản phẩm
        fabAddProduct.setVisibility(View.GONE);
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // Hiển thị email của admin trong header
        View headerView = navigationView.getHeaderView(0);
        TextView tvAdminEmail = headerView.findViewById(R.id.tvAdminEmail);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tvAdminEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    private void setupClickListeners() {
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_products) {
            loadFragment(new ProductsFragment());
            setTitle("Quản lý sản phẩm");
        } else if (id == R.id.nav_users) {
            loadFragment(new UsersFragment());
            setTitle("Quản lý người dùng");
        } else if (id == R.id.nav_statistics) {
            loadFragment(new StatisticsFragment());
            setTitle("Thống kê");
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Xóa dữ liệu người dùng từ SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    
                    // Đăng xuất khỏi Firebase
                    FirebaseAuth.getInstance().signOut();
                    
                    // Chuyển về màn hình đăng nhập
                    Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showAddProductDialog() {
        // Tạo dialog để nhập thông tin sản phẩm mới
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setView(view);

        // Khởi tạo các view trong dialog
        TextInputEditText edtProductName = view.findViewById(R.id.edtProductName);
        TextInputEditText edtProductPrice = view.findViewById(R.id.edtProductPrice);
        TextInputEditText edtProductDescription = view.findViewById(R.id.edtProductDescription);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageView imgProductPhoto = view.findViewById(R.id.imgProductPhoto);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        
        // Reset biến lưu trữ ảnh đã chọn
        selectedImageUri = null;
        currentProductImagePath = null;
        
        // Thiết lập giá trị mặc định
        edtProductName.setText("Sản phẩm mới");
        edtProductPrice.setText("100000");
        edtProductDescription.setText("Mô tả sản phẩm");
        
        // Thiết lập sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

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
            
            // Tạo sản phẩm mới với ID mới
            String newProductId = UUID.randomUUID().toString();
            Double price = Double.parseDouble(priceStr);
            Product newProduct = new Product(newProductId, name, description, price, "", new ArrayList<>());
            
            // Xử lý ảnh sản phẩm nếu có chọn ảnh
            if (selectedImageUri != null) {
                try {
                    // Tạo tên file ảnh mới
                    String imageFileName = "product_" + newProductId + "_" + System.currentTimeMillis();
                    // Lưu ảnh vào bộ nhớ ứng dụng
                    String imagePath = saveImageToInternalStorage(selectedImageUri, imageFileName);
                    
                    // Cập nhật đường dẫn ảnh vào sản phẩm
                    List<String> images = new ArrayList<>();
                    images.add(imagePath);
                    newProduct.setImages(images);
                    
                } catch (IOException e) {
                    Toast.makeText(ManagerActivity.this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            // Thêm sản phẩm vào Firestore
            addProduct(newProduct);
            
            dialog.dismiss();
        });

        dialog.show();
    }
    
    private void loadProducts() {
        showLoading(true);
        // Tìm fragment hiện tại và gọi phương thức loadProducts nếu là ProductsFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ProductsFragment) {
            ((ProductsFragment) currentFragment).loadProducts();
        } else {
            showLoading(false);
        }
    }
    
    private void addProduct(Product newProduct) {
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

    public void showEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setView(view);

        // Khởi tạo các view trong dialog
        TextInputEditText edtProductName = view.findViewById(R.id.edtProductName);
        TextInputEditText edtProductPrice = view.findViewById(R.id.edtProductPrice);
        TextInputEditText edtProductDescription = view.findViewById(R.id.edtProductDescription);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);
        ImageView imgProductPhoto = view.findViewById(R.id.imgProductPhoto);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        
        // Reset biến lưu trữ ảnh đã chọn
        selectedImageUri = null;
        currentProductImagePath = null;

        // Đặt giá trị hiện tại của sản phẩm
        edtProductName.setText(product.getName());
        if (product.getPrice() != null) {
            edtProductPrice.setText(String.valueOf(product.getPrice()));
        }
        edtProductDescription.setText(product.getDescription());
        
        // Hiển thị ảnh sản phẩm nếu có
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imagePath = product.getImages().get(0);
            currentProductImagePath = imagePath;
            if (imagePath.startsWith("drawable/")) {
                // Lấy tên file từ đường dẫn drawable
                String resourceName = imagePath.substring(9); // Bỏ "drawable/"
                int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                if (resourceId != 0) {
                    Glide.with(this)
                            .load(resourceId)
                            .centerCrop()
                            .into(imgProductPhoto);
                }
            }
        }
        
        // Thiết lập sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

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
            
            // Xử lý ảnh sản phẩm nếu có chọn ảnh mới
            if (selectedImageUri != null) {
                try {
                    // Tạo tên file ảnh mới
                    String imageFileName = "product_" + product.getProductId() + "_" + System.currentTimeMillis();
                    // Lưu ảnh vào bộ nhớ ứng dụng
                    String imagePath = saveImageToInternalStorage(selectedImageUri, imageFileName);
                    
                    // Cập nhật đường dẫn ảnh vào sản phẩm
                    List<String> images = product.getImages();
                    if (images == null) {
                        images = new ArrayList<>();
                    } else {
                        images.clear(); // Xóa ảnh cũ nếu có
                    }
                    images.add(imagePath);
                    product.setImages(images);
                    
                } catch (IOException e) {
                    Toast.makeText(ManagerActivity.this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (currentProductImagePath != null) {
                // Giữ nguyên ảnh cũ nếu không chọn ảnh mới
                List<String> images = new ArrayList<>();
                images.add(currentProductImagePath);
                product.setImages(images);
            }

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

    public void showDeleteConfirmationDialog(Product product) {
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
                // Cập nhật lại fragment hiện tại
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof ProductsFragment) {
                    ((ProductsFragment) currentFragment).loadProducts();
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                Toast.makeText(ManagerActivity.this, "Lỗi khi xóa sản phẩm: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại fragment hiện tại nếu cần
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ProductsFragment) {
            ((ProductsFragment) currentFragment).loadProducts();
        }
    }
    
    /**
     * Lưu ảnh từ Uri vào bộ nhớ trong của ứng dụng và trả về đường dẫn drawable
     * @param uri Uri của ảnh đã chọn
     * @param fileName Tên file ảnh
     * @return Đường dẫn drawable của ảnh đã lưu
     */
    private String saveImageToInternalStorage(Uri uri, String fileName) throws IOException {
        // Đọc bitmap từ Uri
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream != null) {
            inputStream.close();
        }
        
        // Tạo đường dẫn file trong thư mục drawable
        String drawablePath = "drawable/" + fileName;
        
        // Lưu bitmap vào bộ nhớ trong
        FileOutputStream fos = null;
        try {
            // Mở file output stream trong thư mục files của ứng dụng
            fos = openFileOutput(fileName, MODE_PRIVATE);
            // Nén bitmap và ghi vào file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        
        return drawablePath;
    }
}