package com.example.storeclothes.ui.Fragment.Cart;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Address;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Adapter.AddressAdapter;
import com.example.storeclothes.ui.Adapter.CartItemAdapter;
import com.example.storeclothes.ui.Fragment.Product.ProductDetailActivity;
import com.example.storeclothes.ui.ViewModel.CartViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnCartItemActionListener {

    private RecyclerView recyclerViewCartItems;
    private CartItemAdapter adapter;
    private FirebaseUser user;

    private TextView tvSubtotal, tvShippingCost, tvTotal, tvRemoveAll;
    private EditText etShippingAddress;
    private FloatingActionButton fabBack;
    private MaterialButton btnCheckout;

    private Address currentAddress = null;
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setOnclickListeners();

        user = FirebaseAuth.getInstance().getCurrentUser();
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        setupObservers();

        if (user != null) {
            cartViewModel.loadCartData(user.getUid());
            cartViewModel.loadAddresses();
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvTotal = findViewById(R.id.tvTotal);
        tvRemoveAll = findViewById(R.id.tvRemoveAll);
        fabBack = findViewById(R.id.fabBack);
        btnCheckout = findViewById(R.id.btnCheckout);
        etShippingAddress = findViewById(R.id.etShippingAddress);

        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(this);
        recyclerViewCartItems.setAdapter(adapter);
    }

    private void setOnclickListeners() {
        tvRemoveAll.setOnClickListener(v -> {
            if (user != null) {
                cartViewModel.clearCart(user.getUid());
            }
        });

        fabBack.setOnClickListener(v -> finish());

        etShippingAddress.setOnClickListener(v -> showCustomAddressDialog());

        btnCheckout.setOnClickListener(v -> {
            if (user != null) {
                if (currentAddress != null) {
                    Double shippingCost = cartViewModel.getShippingCost().getValue();
                    if (shippingCost != null) {
                        cartViewModel.placeOrder(user.getUid(), shippingCost, currentAddress);
                    }
                } else {
                    Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupObservers() {
        cartViewModel.getCartItems().observe(this, cartItems -> {
            adapter.setCartItems(cartItems);
            updateUI();
        });

        cartViewModel.getProductMap().observe(this, productMap -> {
            adapter.setProductMap(productMap);
            updateUI();
        });

        cartViewModel.getSubtotal().observe(this, subtotal -> {
            tvSubtotal.setText(String.format("$%.2f", subtotal != null ? subtotal : 0.0));
        });

        cartViewModel.getShippingCost().observe(this, shippingCost -> {
            tvShippingCost.setText(String.format("$%.2f", shippingCost != null ? shippingCost : 0.0));
        });

        cartViewModel.getTotal().observe(this, total -> {
            tvTotal.setText(String.format("$%.2f", total != null ? total : 0.0));
        });

        cartViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        cartViewModel.getOrderSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        List<CartItem> cartItems = cartViewModel.getCartItems().getValue();
        Map<String, Product> productMap = cartViewModel.getProductMap().getValue();

        if (cartItems == null || cartItems.isEmpty()) {
            tvSubtotal.setText("$0.00");
            tvShippingCost.setText("$0.00");
            tvTotal.setText("$0.00");
        } else if (productMap != null) {
            cartViewModel.updateTotals(cartItems, productMap);
        }
    }

    private void showCustomAddressDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_address);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText etNewAddress = dialog.findViewById(R.id.etNewAddress);
        MaterialButton btnAddNewAddress = dialog.findViewById(R.id.btnAddNewAddress);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AddressAdapter addressAdapter = new AddressAdapter(new ArrayList<>(), address -> {
            etShippingAddress.setText(address.getAddress());
            currentAddress = address;
            cartViewModel.updateShippingCost(address);
            dialog.dismiss();
        });

        recyclerView.setAdapter(addressAdapter);

        cartViewModel.getAddresses().observe(this, addressList -> {
            if (addressList != null) {
                addressAdapter.updateList(addressList);
            }
        });

        btnAddNewAddress.setOnClickListener(v -> {
            String newAddress = etNewAddress.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                Address address = new Address(newAddress);
                cartViewModel.addAddress(address);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng nhập địa chỉ hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> dialog.dismiss());

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            int margin = (int) (20 * getResources().getDisplayMetrics().density);
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);
            window.getDecorView().setPadding(margin, 0, margin, 0);
        }

        dialog.show();
    }

    @Override
    public void onIncreaseQuantity(CartItem item) {
        if (item.getQuantity() < 9) {
            item.setQuantity(item.getQuantity() + 1);
            if (user != null) {
                cartViewModel.updateCartItem(user.getUid(), item);
            }
        } else {
            Toast.makeText(this, "Số lượng tối đa là 9", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDecreaseQuantity(CartItem item) {
        int currentQuantity = item.getQuantity();
        if (currentQuantity > 1) {
            item.setQuantity(currentQuantity - 1);
            if (user != null) {
                cartViewModel.updateCartItem(user.getUid(), item);
            }
        } else if (currentQuantity == 1) {
            item.setQuantity(0);
            if (user != null) {
                cartViewModel.updateCartItem(user.getUid(), item);
            }
            Toast.makeText(this, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOpenProductDetail(String productId) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        intent.putExtra("product_id", productId);
        startActivity(intent);
    }
}