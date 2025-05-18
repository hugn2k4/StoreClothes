package com.example.storeclothes.ui.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Category;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.service.CategoryService;
import com.example.storeclothes.data.service.ProductService;
import com.example.storeclothes.data.service.UserService;
import com.example.storeclothes.ui.Category.CategoryAdapter;
import com.example.storeclothes.ui.Order.OrderActivity;
import com.example.storeclothes.ui.Product.ProductAdapter;
import com.example.storeclothes.ui.Product.ProductDetailActivity;
import com.example.storeclothes.ui.Profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private String userUid;
    private UserService userService;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewCategory, recyclerViewProductTopSelling, recyclerViewProductNewIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userUid = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_uid", null);
        userService = UserService.getInstance();
//        addSampleProducts();
//        addSampleCategories();
        initViews();
        setupRecyclerView();
        loadUserData();
        loadCategoryData();
        loadProductData();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_notification) {
                openActivity(HomeActivity.class);
            } else if (itemId == R.id.nav_order) {
                openActivity(OrderActivity.class);
            } else if (itemId == R.id.nav_profile) {
                openActivity(ProfileActivity.class);
            }
            return true;
        });
    }
    private void addSampleProducts() {
        List<Product> products = Arrays.asList(
                new Product("1",
                        "Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops",
                        "Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday",
                        109.95,
                        "men's clothing",
                        Arrays.asList("https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg")
                ),
                new Product("2",
                        "Mens Casual Premium Slim Fit T-Shirts",
                        "Slim-fitting style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing. And Solid stitched shirts with round neck made for durability and a great fit for casual fashion wear and diehard baseball fans. The Henley style round neckline includes a three-button placket.",
                        22.3,
                        "men's clothing",
                        Arrays.asList("https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg")
                ),
                new Product("3",
                        "Mens Cotton Jacket",
                        "great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day.",
                        55.99,
                        "men's clothing",
                        Arrays.asList("https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg")
                ),
                new Product("4",
                        "Mens Casual Slim Fit",
                        "The color could be slightly different between on the screen and in practice. / Please note that body builds vary by person, therefore, detailed size information should be reviewed below on the product description.",
                        15.99,
                        "men's clothing",
                        Arrays.asList("https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg")
                ),
                new Product("5",
                        "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
                        "From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean's pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.",
                        695.0,
                        "jewelery",
                        Arrays.asList("https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg")
                )
                // Thêm các sản phẩm khác tương tự...
        );

        for (Product p : products) {
            ProductService.getInstance().addProduct(p, new ProductService.OnProductAddListener() {
                @Override
                public void onSuccess() {
                    Log.d("AddSampleProducts", "Thêm sản phẩm " + p.getProductId() + " thành công");
                }

                @Override
                public void onFailure(String error) {
                    Log.e("AddSampleProducts", "Lỗi thêm sản phẩm " + p.getProductId() + ": " + error);
                }
            });
        }
    }
    private void addSampleCategories() {
        List<Category> categories = Arrays.asList(
                new Category("hoodie", "Hoodies", "https://dytbw3ui6vsu6.cloudfront.net/media/catalog/product/resize/780x780/S/a/Sandro_SHPTR00536-30_V_P_1.webp"),
                new Category("tshirt", "T-Shirts", "https://is4.fwrdassets.com/images/p/fw/z/RHUF-MS214_V1.jpg"),
                new Category("trousers", "Trousers", "https://is4.fwrdassets.com/images/p/fw/z/AMIF-MP102_V1.jpg"),
                new Category("shorts", " Shorts", "https://bizweb.dktcdn.net/100/415/697/products/8-981e1e6e-eed0-470e-b2c5-84d41949c8b6.jpg?v=1722571347253"),
                new Category("bags", "Bags", "https://mia.vn/media/uploads/balo-herschel-city-tm-mid-volume-backpack-s-iceberg-green-crosshatch-15852-11735620981.jpg"),
                new Category("jacket", "Jackets", "https://fttleather.com/uploads/1026/product/2022/12/20/novftt5-1-black-1671552020.jpg?v=1.0.01"),
                new Category("shoes", "Shoes", "https://i.ebayimg.com/images/g/kosAAOSw20xgeuTS/s-l1600.webp")
        );

        for (Category category : categories) {
            CategoryService.getInstance().addCategory(category, new CategoryService.OnCategoryAddListener() {
                @Override
                public void onSuccess() {
                    Log.d("AddSampleCategories", "Thêm category " + category.getCategoryId() + " thành công");
                }

                @Override
                public void onFailure(String error) {
                    Log.e("AddSampleCategories", "Lỗi thêm category " + category.getCategoryId() + ": " + error);
                }
            });
        }
    }
    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewProductNewIn = findViewById(R.id.recyclerViewProductsNewIn);
        recyclerViewProductTopSelling = findViewById(R.id.recyclerViewProductsTopSelling);
    }
    private void setupRecyclerView(){
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewProductNewIn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewProductTopSelling.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void loadUserData() {
        if (userUid != null && !userUid.isEmpty()) {
            userService.getUserById(userUid, new UserService.OnUserFetchListener() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        tvGreeting.setText("Hi, " + user.getFirstName() );
                    }
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(HomeActivity.this, "Không thể lấy thông tin người dùng: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvGreeting.setText("Hi, Guest");
        }
    }
    private void loadCategoryData() {
        CategoryService.getInstance().getCategoryListFromFirebase(new CategoryService.OnCategoryListListener() {
            @Override
            public void onSuccess(List<Category> categories) {
                CategoryAdapter categoryAdapter = new CategoryAdapter(HomeActivity.this, categories, category -> {
//                    Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
//                    intent.putExtra("category_id", category.getCategoryId());
//                    startActivity(intent);
                });
                recyclerViewCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(HomeActivity.this, "Không thể lấy danh sách danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadProductData() {
        ProductService.getInstance().getProductListFromFirebase(new ProductService.OnProductListListener() {
            @Override
            public void onSuccess(List<Product> products) {
                ProductAdapter productAdapter = new ProductAdapter(HomeActivity.this, products, product -> {
                    Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                    intent.putExtra("user_id", userUid);
                    intent.putExtra("product_id", product.getProductId());
                    startActivity(intent);
                });
                recyclerViewProductNewIn.setAdapter(productAdapter);
                recyclerViewProductTopSelling.setAdapter(productAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(HomeActivity.this, "Không thể lấy danh sách sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadProductData();
    }
}