package com.example.storeclothes.ui.Admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private final Consumer<String> onDeleteClickListener;

    public ProductAdapter(List<Product> products, Consumer<String> onDeleteClickListener) {
        this.products = products != null ? products : new ArrayList<>();
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateList(List<Product> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProduct;
        private final TextView txtProductName;
        private final TextView txtProductPrice;
        private final ImageButton btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            
            // Chuyển đổi btnFavorite thành nút xóa cho admin
            btnFavorite.setImageResource(R.drawable.ic_delete);
        }

        public void bind(Product product) {
            txtProductName.setText(product.getName());
            txtProductPrice.setText(String.format("$%.2f", product.getPrice()));
            
            // Hiển thị ảnh sản phẩm nếu có
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImages().get(0))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(imgProduct);
            }
            
            // Thiết lập sự kiện xóa sản phẩm
            btnFavorite.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.accept(product.getProductId());
                }
            });
            
            // Thiết lập sự kiện click vào sản phẩm (có thể triển khai sau)
            itemView.setOnClickListener(v -> {
                // Xử lý khi click vào sản phẩm
            });
        }
    }
}