package com.example.storeclothes.ui.Customer.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager implements WishlistSubject {

    private boolean isFavorite = false;
    private final List<WishlistObserver> observers = new ArrayList<>();

    @Override
    public void registerObserver(WishlistObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(WishlistObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (WishlistObserver observer : observers) {
            observer.onWishlistStatusChanged(isFavorite);
        }
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
        notifyObservers();
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
