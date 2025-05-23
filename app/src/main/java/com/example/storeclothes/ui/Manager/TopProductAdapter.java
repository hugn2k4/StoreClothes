package com.example.storeclothes.ui.Manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> products;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public TopProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(currencyFormat.format(product.getPrice()));
        holder.tvRank.setText(String.valueOf(position + 1));
        
        // Hiển thị ảnh sản phẩm nếu có
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imagePath = product.getImages().get(0);
            if (imagePath.startsWith("drawable/")) {
                // Lấy tên file từ đường dẫn drawable
                String resourceName = imagePath.substring(9); // Bỏ "drawable/"
                int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    Glide.with(context)
                            .load(resourceId)
                            .centerCrop()
                            .into(holder.imgProduct);
                }
            } else {
                Glide.with(context)
                        .load(imagePath)
                        .centerCrop()
                        .placeholder(R.drawable.ic_loading)
                        .into(holder.imgProduct);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_loading);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvRank;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvRank = itemView.findViewById(R.id.tvRank);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}