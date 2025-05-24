package com.example.storeclothes.ui.Fragment.Cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.Address;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Order;
import com.example.storeclothes.data.model.OrderItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.repository.CartRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartViewModel extends ViewModel {

    private final CartRepository cartRepository;
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Product>> productMap = new MutableLiveData<>();
    private final MutableLiveData<List<Address>> addresses = new MutableLiveData<>();
    private final MutableLiveData<Double> subtotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> shippingCost = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> total = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cartUpdateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addressAddSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addToCartSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> addToCartError = new MutableLiveData<>();

    public CartViewModel() {
        cartRepository = CartRepository.getInstance();
    }

    public void addToCart(String userId, CartItem item) {
        cartRepository.addToCart(userId, item)
                .addOnSuccessListener(aVoid -> {
                    addToCartSuccess.setValue(true);
                    loadCartData(userId); // Refresh cart to reflect new item
                })
                .addOnFailureListener(e -> addToCartError.setValue("Lỗi khi thêm vào giỏ hàng: " + e.getMessage()));
    }

    public void loadCartData(String userId) {
        cartRepository.getCartItems(userId).observeForever(items -> {
            cartItems.setValue(items);
            if (items == null || items.isEmpty()) {
                updateTotals(new ArrayList<>(), new HashMap<>());
                return;
            }
            List<String> productIds = items.stream()
                    .map(CartItem::getProductId)
                    .collect(Collectors.toList());
            cartRepository.getProductsByIds(productIds, products -> {
                productMap.setValue(products);
                updateTotals(items, products);
            }, errorMsg -> error.setValue("Lỗi tải sản phẩm: " + errorMsg));
        });
    }

    public void loadAddresses() {
        cartRepository.getAddresses().observeForever(addresses::setValue);
    }

    public void addAddress(Address address) {
        cartRepository.addAddress(address)
                .addOnSuccessListener(docRef -> {
                    addressAddSuccess.setValue(true);
                    loadAddresses();
                })
                .addOnFailureListener(e -> error.setValue("Lưu địa chỉ thất bại: " + e.getMessage()));
    }

    public void updateCartItem(String userId, CartItem updatedItem) {
        List<CartItem> currentItems = cartItems.getValue() != null ? new ArrayList<>(cartItems.getValue()) : new ArrayList<>();
        if (updatedItem.getQuantity() == 0) {
            currentItems.removeIf(item -> item.getProductId().equals(updatedItem.getProductId()) &&
                    item.getSize().equals(updatedItem.getSize()) &&
                    item.getColor().equals(updatedItem.getColor()));
        } else {
            boolean found = false;
            for (int i = 0; i < currentItems.size(); i++) {
                CartItem item = currentItems.get(i);
                if (item.getProductId().equals(updatedItem.getProductId()) &&
                        item.getSize().equals(updatedItem.getSize()) &&
                        item.getColor().equals(updatedItem.getColor())) {
                    currentItems.set(i, updatedItem);
                    found = true;
                    break;
                }
            }
            if (!found) {
                currentItems.add(updatedItem);
            }
        }
        cartRepository.updateCartItems(userId, currentItems)
                .addOnSuccessListener(aVoid -> {
                    cartUpdateSuccess.setValue(true);
                    updateTotals(currentItems, productMap.getValue());
                })
                .addOnFailureListener(e -> error.setValue("Cập nhật giỏ hàng thất bại: " + e.getMessage()));
    }

    public void clearCart(String userId) {
        cartRepository.clearCart(userId)
                .addOnSuccessListener(aVoid -> {
                    cartItems.setValue(new ArrayList<>());
                    updateTotals(new ArrayList<>(), productMap.getValue());
                    cartUpdateSuccess.setValue(true);
                })
                .addOnFailureListener(e -> error.setValue("Xoá giỏ hàng thất bại: " + e.getMessage()));
    }

    public void placeOrder(String userId, double shippingCost, Address address) {
        List<CartItem> items = cartItems.getValue();
        Map<String, Product> products = productMap.getValue();
        if (items == null || items.isEmpty()) {
            error.setValue("Giỏ hàng trống");
            return;
        }
        if (address == null) {
            error.setValue("Vui lòng chọn địa chỉ giao hàng");
            return;
        }
        double subtotal = calculateSubtotal(items, products);
        double totalPrice = subtotal + shippingCost;
        List<OrderItem> orderItems = items.stream()
                .map(OrderItem::new)
                .collect(Collectors.toList());
        Order order = new Order.Builder()
                .setUserId(userId)
                .setItems(orderItems)
                .setTotalAmount(totalPrice)
                .setShippingFee(shippingCost)
                .setStatus("pending")
                .setOrderDate(new Date())
                .build();
        cartRepository.placeOrder(order)
                .addOnSuccessListener(docRef -> {
                    orderSuccess.setValue(true);
                    clearCart(userId);
                })
                .addOnFailureListener(e -> error.setValue("Đặt hàng thất bại: " + e.getMessage()));
    }

    public void updateShippingCost(Address address) {
        if (address == null) {
            shippingCost.setValue(0.0);
            updateTotals(cartItems.getValue(), productMap.getValue());
            return;
        }
        String addressValue = address.getAddress().trim();
        double cost;
        if (addressValue.equalsIgnoreCase("Hà Nội")) {
            cost = 10.0;
        } else if (addressValue.equalsIgnoreCase("Hồ Chí Minh")) {
            cost = 15.0;
        } else {
            cost = 20.0;
        }
        shippingCost.setValue(cost);
        updateTotals(cartItems.getValue(), productMap.getValue());
    }

    public void updateTotals(List<CartItem> items, Map<String, Product> products) {
        double sub = calculateSubtotal(items, products);
        double ship = shippingCost.getValue() != null ? shippingCost.getValue() : 0.0;
        double totalAmount = sub > 0 ? sub + ship : 0.0;
        subtotal.setValue(sub);
        total.setValue(totalAmount);
    }

    private double calculateSubtotal(List<CartItem> items, Map<String, Product> products) {
        if (items == null || products == null) return 0.0;
        double sub = 0.0;
        for (CartItem item : items) {
            Product product = products.get(item.getProductId());
            if (product != null) {
                sub += product.getPrice() * item.getQuantity();
            }
        }
        return sub;
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Map<String, Product>> getProductMap() {
        return productMap;
    }

    public LiveData<List<Address>> getAddresses() {
        return addresses;
    }

    public LiveData<Double> getSubtotal() {
        return subtotal;
    }

    public LiveData<Double> getShippingCost() {
        return shippingCost;
    }

    public LiveData<Double> getTotal() {
        return total;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getOrderSuccess() {
        return orderSuccess;
    }

    public LiveData<Boolean> getCartUpdateSuccess() {
        return cartUpdateSuccess;
    }

    public LiveData<Boolean> getAddressAddSuccess() {
        return addressAddSuccess;
    }

    public LiveData<Boolean> getAddToCartSuccess() {
        return addToCartSuccess;
    }

    public LiveData<String> getAddToCartError() {
        return addToCartError;
    }
}