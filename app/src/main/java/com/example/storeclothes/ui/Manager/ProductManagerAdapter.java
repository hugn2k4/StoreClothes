package com.example.storeclothes.ui.Manager;

import android.content.Context;
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

import java.util.List;

public class ProductManagerAdapter extends RecyclerView.Adapter<ProductManagerAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
        void onItemClick(Product product);
    }

    public ProductManagerAdapter(Context context, List<Product> productList, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_manager, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Load ảnh từ danh sách ảnh đầu tiên (nếu có)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imagePath = product.getImages().get(0);
            if (imagePath.startsWith("drawable/")) {
                // Lấy tên file từ đường dẫn drawable
                String resourceName = imagePath.substring(9); // Bỏ "drawable/"
                int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    Glide.with(context)
                            .load(resourceId)
                            .placeholder(R.drawable.ic_loading)
                            .into(holder.imgProduct);
                } else {
                    holder.imgProduct.setImageResource(R.drawable.ic_loading);
                }
            } else {
                // Nếu là URL hoặc đường dẫn khác
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_loading)
                        .into(holder.imgProduct);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_loading);
        }

        holder.txtProductName.setText(product.getName());

        if (product.getPrice() != null) {
            holder.txtProductPrice.setText(String.format("%,.0f VND", product.getPrice()));
        } else {
            holder.txtProductPrice.setText("Chưa có giá");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(product);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateData(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice;
        ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}