package com.example.storeclothes.ui.Cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Cart;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Product.ProductDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    private FloatingActionButton fabBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(this);
        recyclerViewCartItems.setAdapter(adapter);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvTotal = findViewById(R.id.tvTotal);
        tvRemoveAll = findViewById(R.id.tvRemoveAll);
        tvRemoveAll.setOnClickListener(v -> {
            removeAllCartItems();
        });
        fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v -> {
            finish();
        });

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadCartItems(user.getUid());
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
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
                    calculateAndDisplayTotals(adapter.getCartItems(), productMap);
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
                    calculateAndDisplayTotals(cartItems, productMap);

                    Toast.makeText(this, "Đã xoá tất cả sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Xoá thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void calculateAndDisplayTotals(List<CartItem> cartItems, Map<String, Product> productMap) {
        double subtotal = 0.0;

        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                subtotal += product.getPrice() * item.getQuantity();
            }
        }

        // Giả sử phí vận chuyển cố định 8 đô
        double shippingCost = subtotal > 0 ? 8.0 : 0.0;
        double total = subtotal + shippingCost;

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
                        calculateAndDisplayTotals(cartItems, productMap);
                        Toast.makeText(this, "Cập nhật giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Cập nhật giỏ hàng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
