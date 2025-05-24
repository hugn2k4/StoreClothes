package com.example.storeclothes.ui.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.ui.ViewModel.ProductViewModel;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;
    private String userUid;
    private ProductViewModel productViewModel;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.userUid = context.getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_uid", null);

        if (context instanceof AppCompatActivity) {
            productViewModel = new ViewModelProvider((AppCompatActivity) context).get(ProductViewModel.class);
        }
    }

    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Load product image
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.ic_loading)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_loading);
        }

        holder.txtProductName.setText(product.getName());
        holder.txtProductPrice.setText(product.getPrice() != null
                ? "$" + String.format("%,d", product.getPrice().intValue())
                : "Price not available");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        setupFavoriteButton(holder, product);
    }

    private void setupFavoriteButton(ProductViewHolder holder, Product product) {
        if (userUid == null || productViewModel == null) {
            holder.btnFavorite.setVisibility(View.GONE);
            return;
        }

        // Kiểm tra trạng thái wishlist
        productViewModel.checkWishlistStatus(userUid, product.getProductId())
                .observe((LifecycleOwner) context, isFavorite -> {
                    if (isFavorite != null) {
                        updateFavoriteButton(holder, isFavorite);
//                        setupFavoriteClickListener(holder, product, isFavorite);
                    }
                });
        holder.btnFavorite.setOnClickListener(v -> {
            boolean currentFavorite = holder.btnFavorite.getTag() != null && (boolean) holder.btnFavorite.getTag();
            String wishlistId = userUid + "_" + product.getProductId();
            Wishlist wishlist = new Wishlist.Builder()
                    .setWishlistId(wishlistId)
                    .setUserId(userUid)
                    .setProductId(product.getProductId())
                    .build();

            if (currentFavorite) {
                productViewModel.removeFromWishlist(wishlistId)
                        .observe((LifecycleOwner) context, success -> {
                            if (success) {
                                updateFavoriteButton(holder, false);
                            }
                        });
            } else {
                productViewModel.addToWishlist(wishlist)
                        .observe((LifecycleOwner) context, success -> {
                            if (success) {
                                updateFavoriteButton(holder, true);
                            }
                        });
            }
        });
    }

    private void updateFavoriteButton(ProductViewHolder holder, boolean isFavorite) {
        holder.btnFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );
        holder.btnFavorite.setTag(isFavorite);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice;
        ImageButton btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}