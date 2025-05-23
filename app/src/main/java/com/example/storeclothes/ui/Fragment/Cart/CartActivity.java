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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Address;
import com.example.storeclothes.data.model.Cart;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Fragment.Cart.AddressAdapter;
import com.example.storeclothes.ui.Fragment.Cart.CartItemAdapter;
import com.example.storeclothes.ui.Fragment.Product.ProductDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnCartItemActionListener {

    private RecyclerView recyclerViewCartItems;
    private CartItemAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private List<CartItem> cartItems = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();

    private TextView tvSubtotal, tvShippingCost, tvTotal, tvRemoveAll;
    private EditText etShippingAddress;
    private FloatingActionButton fabBack;
    private Address currentAddress = null;
    private double currentShippingCost = 0.0;
    private MaterialButton btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initViews();
        setOnclickListeners();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadCartItems(user.getUid());
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }
    private void initViews(){
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvShippingCost.setText(String.format("$%.2f", currentShippingCost));
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
            removeAllCartItems();
        });
        fabBack.setOnClickListener(v -> {
            finish();
        });
        etShippingAddress.setOnClickListener(v -> showCustomAddressDialog());
        btnCheckout.setOnClickListener(v -> placeOrder());
    }
    private void placeOrder() {
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAddress == null || etShippingAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                subtotal += product.getPrice() * item.getQuantity();
            }
        }
        double totalPrice = subtotal + currentShippingCost;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItems.add(new OrderItem(cartItem));
        }
        Order order = new Order.Builder()
                .setUserId(user.getUid())
                .setItems(orderItems)
                .setTotalAmount(totalPrice)
                .setShippingFee(currentShippingCost)
                .setStatus("pending")
                .setOrderDate(new Date())
                .build();

        // Thêm order vào Firestore collection "orders"
        db.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    clearCartAfterOrder();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Đặt hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCartAfterOrder() {
        if (user == null) return;

        DocumentReference cartRef = db.collection("carts").document(user.getUid());
        cartRef.update("cartItems", new ArrayList<>())
                .addOnSuccessListener(aVoid -> {
                    cartItems.clear();
                    adapter.setCartItems(cartItems);
                    calculateAndDisplayTotals(cartItems, productMap, 0);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Xoá giỏ hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
            EditText etShippingAddress = findViewById(R.id.etShippingAddress);
            etShippingAddress.setText(address.getAddress());
            updateShippingCost(address);
            dialog.dismiss();
        });

        recyclerView.setAdapter(addressAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Load địa chỉ từ collection chung "addresses"
        db.collection("addresses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Address> addressList = new ArrayList<>();
                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        Address address = doc.toObject(Address.class);
                        if (address != null) {
                            address.setId(doc.getId()); // nếu Address có trường id
                            addressList.add(address);
                        }
                    }
                    addressAdapter.updateList(addressList);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tải địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        btnAddNewAddress.setOnClickListener(v -> {
            String newAddress = etNewAddress.getText().toString().trim();

            if (!newAddress.isEmpty()) {
                Address address = new Address(newAddress); // giả sử constructor Address(String address)

                // Thêm vào Firestore collection chung "addresses"
                db.collection("addresses")
                        .add(address)
                        .addOnSuccessListener(documentReference -> {
                            address.setId(documentReference.getId());
                            addressAdapter.addAddress(address);
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lưu địa chỉ thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Vui lòng nhập địa chỉ hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> dialog.dismiss());

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            int margin = (int) (20 * getResources().getDisplayMetrics().density); // 20dp to pixels
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
            window.setGravity(Gravity.CENTER);
            window.getDecorView().setPadding(margin, 0, margin, 0);
        }

        dialog.show();
    }

    private void updateShippingCost(Address address) {
        currentAddress = address;
        String addressValue = address.getAddress().trim();
        if (addressValue.equalsIgnoreCase("Hà Nội")) {
            currentShippingCost = 10.0;
        } else if (addressValue.equalsIgnoreCase("Hồ Chí Minh")) {
            currentShippingCost = 15.0;
        } else {
            currentShippingCost = 20.0;
        }
        tvShippingCost.setText(String.format("$%.2f", currentShippingCost));
        calculateAndDisplayTotals(adapter.getCartItems(), productMap, currentShippingCost);
    }
    private void loadCartItems(String userId) {
        DocumentReference cartRef = db.collection("carts").document(userId);

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Cart cart = documentSnapshot.toObject(Cart.class);
                if (cart != null && cart.getCartItems() != null) {
                    cartItems = cart.getCartItems();
                    adapter.setCartItems(cartItems);

                    List<String> productIds = new ArrayList<>();
                    for (CartItem item : cartItems) {
                        productIds.add(item.getProductId());
                    }

                    loadProducts(productIds);
                } else {
                    Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Chưa có giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void loadProducts(List<String> productIds) {
        if (productIds.isEmpty()) return;

        // Firestore có giới hạn max 10 phần tử trong whereIn, nên nếu nhiều hơn bạn phải chia nhỏ (ở đây giả định ít hơn 10)
        db.collection("products")
                .whereIn("productId", productIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productMap.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Product product = doc.toObject(Product.class);
                        if (product != null && product.getProductId() != null) {
                            productMap.put(product.getProductId(), product);
                        }
                    }
                    adapter.setProductMap(productMap);
                    calculateAndDisplayTotals(adapter.getCartItems(), productMap, currentShippingCost);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void removeAllCartItems() {
        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xoá giỏ hàng trên Firestore
        DocumentReference cartRef = db.collection("carts").document(user.getUid());

        // Cập nhật cartItems thành danh sách rỗng
        cartRef.update("cartItems", new ArrayList<>())
                .addOnSuccessListener(aVoid -> {
                    // Xoá local cartItems
                    cartItems.clear();

                    // Cập nhật adapter và UI
                    adapter.setCartItems(cartItems);

                    // Cập nhật subtotal, total, shipping...
                    calculateAndDisplayTotals(cartItems, productMap, currentShippingCost);

                    Toast.makeText(this, "Đã xoá tất cả sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Xoá thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void calculateAndDisplayTotals(List<CartItem> cartItems, Map<String, Product> productMap, double shippingCost) {
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                subtotal += product.getPrice() * item.getQuantity();
            }
        }

        double total = subtotal > 0 ? subtotal + shippingCost : 0.0;

        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvShippingCost.setText(String.format("$%.2f", shippingCost));
        tvTotal.setText(String.format("$%.2f", total));
    }

    // Xử lý tăng số lượng
    @Override
    public void onIncreaseQuantity(CartItem item) {
        if (item.getQuantity() < 9) { // Giới hạn số lượng tối đa 9
            item.setQuantity(item.getQuantity() + 1);
            updateCartItem(item);
        } else {
            Toast.makeText(this, "Số lượng tối đa là 9", Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý giảm số lượng
    @Override
    public void onDecreaseQuantity(CartItem item) {
        int currentQuantity = item.getQuantity();
        if (currentQuantity > 1) {
            item.setQuantity(currentQuantity - 1);
            updateCartItem(item);
        } else if (currentQuantity == 1) {
            item.setQuantity(0);
            updateCartItem(item);
            Toast.makeText(this, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    // Mở chi tiết sản phẩm
    @Override
    public void onOpenProductDetail(String productId) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        intent.putExtra("product_id", productId);
        startActivity(intent);
    }

    // Cập nhật lại cartItem lên Firestore và cập nhật lại adapter
    private void updateCartItem(CartItem item) {
        // Nếu số lượng bằng 0 thì xóa khỏi danh sách cartItems
        if (item.getQuantity() == 0) {
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem cartItem = cartItems.get(i);
                if (cartItem.getProductId().equals(item.getProductId()) &&
                        cartItem.getSize().equals(item.getSize()) &&
                        cartItem.getColor().equals(item.getColor())) {
                    cartItems.remove(i);
                    break;
                }
            }
        } else {
            // Cập nhật item trong cartItems list
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem cartItem = cartItems.get(i);
                if (cartItem.getProductId().equals(item.getProductId()) &&
                        cartItem.getSize().equals(item.getSize()) &&
                        cartItem.getColor().equals(item.getColor())) {
                    cartItems.set(i, item);
                    break;
                }
            }
        }

        // Cập nhật lên Firestore
        if (user != null) {
            DocumentReference cartRef = db.collection("carts").document(user.getUid());
            cartRef.update("cartItems", cartItems)
                    .addOnSuccessListener(aVoid -> {
                        adapter.setCartItems(cartItems);
                        calculateAndDisplayTotals(cartItems, productMap, currentShippingCost);
                        Toast.makeText(this, "Cập nhật giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Cập nhật giỏ hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}