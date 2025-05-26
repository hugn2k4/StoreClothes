package com.example.storeclothes.ui.Fragment.Wishlist;

public interface WishlistSubject {
    void registerObserver(WishlistObserver observer);
    void removeObserver(WishlistObserver observer);
    void notifyObservers();
}