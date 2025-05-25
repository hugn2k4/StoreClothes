package com.example.storeclothes.ui.Strategy;

public interface EntityActionStrategy {
    void add(Entity entity);
    void edit(Entity entity);
    void delete(Entity entity);
    void toggleStatus(Entity entity);
}