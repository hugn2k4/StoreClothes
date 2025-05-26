package com.example.storeclothes.ui.Customer.Wishlist;

public interface WishlistSubject {
    void registerObserver(WishlistObserver observer);
    void removeObserver(WishlistObserver observer);
    void notifyObservers();
}