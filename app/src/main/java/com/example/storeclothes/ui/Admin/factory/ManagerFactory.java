package com.example.storeclothes.ui.Admin.factory;

import com.example.storeclothes.ui.Admin.manager.BaseManager;
import com.example.storeclothes.ui.Admin.manager.ProductManager;
import com.example.storeclothes.ui.Admin.manager.UserManager;

/**
 * Factory tạo các đối tượng quản lý
 * Áp dụng Factory Method Pattern
 */
public class ManagerFactory {
    public enum ManagerType {
        USER,
        PRODUCT
    }
    
    public static BaseManager createManager(ManagerType type) {
        switch (type) {
            case USER:
                return new UserManager();
            case PRODUCT:
                return new ProductManager();
            default:
                throw new IllegalArgumentException("Unknown manager type: " + type);
        }
    }
}   