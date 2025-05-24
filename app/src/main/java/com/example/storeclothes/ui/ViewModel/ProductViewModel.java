package com.example.storeclothes.ui.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.storeclothes.data.model.CartItem;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.model.Review;
import com.example.storeclothes.data.model.Wishlist;
import com.example.storeclothes.data.repository.CartRepository;
import com.example.storeclothes.data.repository.ProductRepository;
import com.example.storeclothes.data.repository.ReviewRepository;
import com.example.storeclothes.data.repository.WishlistRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;
    private final CartRepository cartRepository;
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<String> productError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> wishlistStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> wishlistActionSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> wishlistActionError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addToCartSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> addToCartError = new MutableLiveData<>();
    private final ReviewRepository reviewRepository;
    private final MutableLiveData<List<Review>> reviewList = new MutableLiveData<>();

    public ProductViewModel() {
        reviewRepository = ReviewRepository.getInstance();
        productRepository = ProductRepository.getInstance();
        wishlistRepository = WishlistRepository.getInstance();
        cartRepository = CartRepository.getInstance();
    };
    public LiveData<List<Review>> getReviewList() {
        return reviewList;
    }

    public void loadReviews(String productId) {
        reviewRepository.getReviewDetailsByProductId(productId)
                .observeForever(reviews -> reviewList.setValue(reviews));
    }
    public void loadProductDetails(String productId) {
        productRepository.getProductById(productId).observeForever(p -> {
            if (p != null) {
                product.setValue(p);
            } else {
                productError.setValue("Không thể lấy sản phẩm");
            }
        });
    }

    public LiveData<List<Product>> getProductsByCategory(String categoryId) {
        return productRepository.getProductsByCategory(categoryId);
    }

    public LiveData<Boolean> checkWishlistStatus(String userId, String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        wishlistRepository.checkWishlistStatus(userId, productId)
                .observeForever(result::setValue);
        return result;
    }

    public LiveData<Boolean> addToWishlist(Wishlist wishlist) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        wishlistRepository.addToWishlist(wishlist)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    public LiveData<Boolean> removeFromWishlist(String wishlistId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        wishlistRepository.removeFromWishlist(wishlistId)
                .addOnSuccessListener(aVoid -> result.setValue(true))
                .addOnFailureListener(e -> result.setValue(false));
        return result;
    }

    public void addToCart(String userId, CartItem item) {
        cartRepository.addToCart(userId, item)
                .addOnSuccessListener(aVoid -> addToCartSuccess.setValue(true))
                .addOnFailureListener(e -> addToCartError.setValue("Lỗi khi thêm vào giỏ hàng: " + e.getMessage()));
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<String> getProductError() {
        return productError;
    }

    public LiveData<Boolean> getWishlistStatus() {
        return wishlistStatus;
    }

    public LiveData<Boolean> getWishlistActionSuccess() {
        return wishlistActionSuccess;
    }

    public LiveData<String> getWishlistActionError() {
        return wishlistActionError;
    }

    public LiveData<Boolean> getAddToCartSuccess() {
        return addToCartSuccess;
    }

    public LiveData<String> getAddToCartError() {
        return addToCartError;
    }
}