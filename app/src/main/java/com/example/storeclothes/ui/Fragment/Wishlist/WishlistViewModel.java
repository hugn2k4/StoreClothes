package com.example.storeclothes.ui.Fragment.Wishlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.data.repository.ProductRepository;
import com.example.storeclothes.data.repository.WishlistRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WishlistViewModel extends ViewModel {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final MutableLiveData<List<Product>> wishlistProducts = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> removeAllSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> removeAllError = new MutableLiveData<>();

    public WishlistViewModel() {
        wishlistRepository = WishlistRepository.getInstance();
        productRepository = ProductRepository.getInstance();
    }

    public void loadWishlistProducts(String userId) {
        wishlistRepository.getWishlistByUser(userId).observeForever(wishlists -> {
            if (wishlists == null || wishlists.isEmpty()) {
                wishlistProducts.setValue(new ArrayList<>());
                return;
            }
            List<String> productIds = wishlists.stream()
                    .map(Wishlist::getProductId)
                    .collect(Collectors.toList());
            productRepository.getProductsByIds(productIds, products -> {
                wishlistProducts.setValue(products);
            }, errorMsg -> error.setValue("Lỗi khi lấy sản phẩm: " + errorMsg));
        });
    }

    public void removeAllWishlistItems(String userId) {
        wishlistRepository.getWishlistByUser(userId).observeForever(wishlists -> {
            if (wishlists == null || wishlists.isEmpty()) {
                removeAllSuccess.setValue(true);
                return;
            }
            for (Wishlist wishlist : wishlists) {
                wishlistRepository.removeFromWishlist(wishlist.getWishlistId())
                        .addOnSuccessListener(aVoid -> {
                            // Wait for all deletions to complete
                            if (wishlists.indexOf(wishlist) == wishlists.size() - 1) {
                                removeAllSuccess.setValue(true);
                            }
                        })
                        .addOnFailureListener(e -> removeAllError.setValue("Lỗi khi xoá: " + e.getMessage()));
            }
        });
    }

    public LiveData<List<Product>> getWishlistProducts() {
        return wishlistProducts;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getRemoveAllSuccess() {
        return removeAllSuccess;
    }

    public LiveData<String> getRemoveAllError() {
        return removeAllError;
    }
}