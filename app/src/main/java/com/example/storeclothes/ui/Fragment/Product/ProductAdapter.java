package com.example.storeclothes.ui.Fragment.Product;

import static android.content.Context.MODE_PRIVATE;

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
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.data.service.WishlistService;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;
    private String userUid;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        userUid = context.getSharedPreferences("user_prefs", MODE_PRIVATE).getString("user_uid", null);
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

        // Load ảnh từ danh sách ảnh đầu tiên (nếu có)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(context)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.ic_loading)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_loading);
        }

        holder.txtProductName.setText(product.getName());

        if (product.getPrice() != null) {
            holder.txtProductPrice.setText("$" + String.format("%,d", product.getPrice().intValue()));
        } else {
            holder.txtProductPrice.setText("Price not available");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        String wishlistId = userUid + "_" + product.getProductId();
        WishlistService.getInstance().getWishlistByUser(userUid, new WishlistService.OnWishlistListListener() {
            @Override
            public void onSuccess(List<Wishlist> wishlists) {
                boolean isFavorite = false;
                for (Wishlist w : wishlists) {
                    if (w.getProductId().equals(product.getProductId())) {
                        isFavorite = true;
                        break;
                    }
                }
                holder.btnFavorite.setTag(isFavorite);

                holder.btnFavorite.setImageResource(
                        isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
                );

                holder.btnFavorite.setOnClickListener(v -> {
                    boolean currentFavorite = (boolean) holder.btnFavorite.getTag();
                    if (currentFavorite) {
                        // Xoá
                        WishlistService.getInstance().removeFromWishlist(wishlistId, new WishlistService.OnWishlistActionListener() {
                            @Override
                            public void onSuccess() {
                                holder.btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                                holder.btnFavorite.setTag(false); // cập nhật lại
                            }

                            @Override
                            public void onFailure(String errorMessage) {}
                        });
                    } else {
                        // Thêm
                        Wishlist wishlist = new Wishlist(wishlistId, userUid, product.getProductId());
                        WishlistService.getInstance().addToWishlist(wishlist, new WishlistService.OnWishlistActionListener() {
                            @Override
                            public void onSuccess() {
                                holder.btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
                                holder.btnFavorite.setTag(true); // cập nhật lại
                            }
                            @Override
                            public void onFailure(String errorMessage) {}
                        });
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
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
