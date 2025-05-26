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
    private Consumer<Product> onEditClickListener;

    public ProductAdapter(List<Product> products, Consumer<String> onDeleteClickListener) {
        this.products = products != null ? products : new ArrayList<>();
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnItemClickListener(Consumer<Product> onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void updateList(List<Product> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_manager, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    public void removeProductById(String productId) {
        if (products == null || products.isEmpty()) return;

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equals(productId)) {
                products.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvProductName, tvProductCategory, tvProductPrice, tvProductStock, tvProductSales;
        private final ImageView imgProduct;
        private final ImageButton btnEditProduct, btnDeleteProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
            tvProductSales = itemView.findViewById(R.id.tvProductSales);
            imgProduct = itemView.findViewById(R.id.imgProduct); // Bạn cần thêm ImageView vào layout nếu muốn hiển thị ảnh
            btnEditProduct = itemView.findViewById(R.id.btnEditProduct);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
        }

        public void bind(Product product) {
            tvProductName.setText(product.getName());
            tvProductCategory.setText(product.getCategoryId() != null ? product.getCategoryId() : "Unknown");
            tvProductPrice.setText(String.format("$%.2f", product.getPrice() != null ? product.getPrice() : 0.0));
            tvProductStock.setText("0"); // Vì model Product hiện không có trường stock
            tvProductSales.setText("0"); // Vì model Product hiện không có trường sold/sales

            // Load ảnh sản phẩm nếu có
            if (product.getImages() != null && !product.getImages().isEmpty()
                    && product.getImages().get(0) != null) {
                Glide.with(itemView.getContext())
                        .load(product.getImages().get(0))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.placeholder_image);
            }

            btnEditProduct.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.accept(product);
                }
            });

            btnDeleteProduct.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.accept(product.getProductId());
                }
            });
        }
    }
}
