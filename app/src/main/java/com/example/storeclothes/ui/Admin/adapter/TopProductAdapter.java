package com.example.storeclothes.ui.Admin.adapter;

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
import java.util.Map;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.TopProductViewHolder> {
    private List<Product> products;
    private Map<String, Integer> quantityMap;
    private NumberFormat currencyFormatter;

    public TopProductAdapter(List<Product> products, Map<String, Integer> quantityMap) {
        this.products = products;
        this.quantityMap = quantityMap;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
    }

    @NonNull
    @Override
    public TopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_product, parent, false);
        return new TopProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopProductViewHolder holder, int position) {
        Product product = products.get(position);
        Integer soldQuantity = quantityMap.get(product.getProductId());
        
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(currencyFormatter.format(product.getPrice()));
        holder.tvSoldQuantity.setText("Đã bán: " + soldQuantity);
        
        // Hiển thị hình ảnh sản phẩm nếu có
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.placeholder_image);
        }
        
        // Hiển thị thứ hạng
        holder.tvRank.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class TopProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvSoldQuantity, tvRank;

        public TopProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSoldQuantity = itemView.findViewById(R.id.tvSoldQuantity);
            tvRank = itemView.findViewById(R.id.tvRank);
        }
    }
}