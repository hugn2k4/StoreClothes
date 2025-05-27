package com.example.storeclothes.ui.Admin.command;

import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Admin.factory.ManagerFactory;
import com.example.storeclothes.ui.Admin.manager.BaseManager;

public class DeleteProductCommand implements Command {
    private final String productId;
    private Product deletedProduct;
    private final BaseManager<Product> productManager;

    public DeleteProductCommand(String productId) {
        this.productId = productId;
        this.productManager = ManagerFactory.createManager(ManagerFactory.ManagerType.PRODUCT);
    }

    @Override
    public void delete() {
        // Lưu product trước khi xóa để có thể hoàn tác
        productManager.getById(productId).observeForever(product -> {
            if (product != null) {
                deletedProduct = product;
                productManager.delete(productId);
            }
        });
    }

    @Override
    public void restore() {
        if (deletedProduct != null) {
            productManager.add(deletedProduct);
        }
    }

    @Override
    public String getDescription() {
        return "Delete product with ID: " + productId;
    }
}