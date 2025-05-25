package com.example.storeclothes.ui.Admin.manager;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Lớp cơ sở cho các Manager
 */
public abstract class BaseManager<T> {
    public abstract LiveData<List<T>> getAll();
    public abstract LiveData<T> getById(String id);
    public abstract LiveData<Boolean> add(T item);
    public abstract LiveData<Boolean> update(T item);
    public abstract LiveData<Boolean> delete(String id);
}