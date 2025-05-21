package com.example.storeclothes.ui.Cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();

    // Interface callback để xử lý sự kiện từ adapter
    public interface OnCartItemActionListener {
        void onIncreaseQuantity(CartItem item);
        void onDecreaseQuantity(CartItem item);
        void onOpenProductDetail(String productId);
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    private OnCartItemActionListener listener;

    public CartItemAdapter(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    public void setProductMap(Map<String, Product> map) {
        this.productMap = map;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Product product = productMap.get(item.getProductId());
        holder.bind(item, product, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvSize, tvColor, tvPrice;
        ImageView ivProductImage;
        MaterialButton btnIncrease, btnDecrease;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvColor = itemView.findViewById(R.id.tvColor);
            ivProductImage = itemView.findViewById(R.id.imageProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }

        public void bind(CartItem item, Product product, OnCartItemActionListener listener) {
            if (product != null) {
                tvProductName.setText(product.getName());
                tvPrice.setText(String.format("$%.2f", product.getPrice() * item.getQuantity()));

                List<String> images = product.getImages();
                if (images != null && !images.isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(images.get(0))
                            .placeholder(R.drawable.ic_loading)
                            .into(ivProductImage);
                } else {
                    ivProductImage.setImageResource(R.drawable.ic_loading);
                }
            } else {
                tvProductName.setText("Unknown product");
                ivProductImage.setImageResource(R.drawable.ic_loading);
                tvPrice.setText("$0");
            }

            tvQuantity.setText(String.valueOf(item.getQuantity()));
            tvSize.setText(item.getSize());
            tvColor.setText(item.getColor());

            // Xử lý nút tăng số lượng
            btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIncreaseQuantity(item);
                }
            });

            // Xử lý nút giảm số lượng
            btnDecrease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDecreaseQuantity(item);
                }
            });

            // Click mở chi tiết sản phẩm
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOpenProductDetail(item.getProductId());
                }
            });
        }
    }
}
