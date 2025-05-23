package com.example.storeclothes.data.repository;

import com.example.storeclothes.data.model.Product;

public class ProductCommand implements Command {
    public enum ActionType { DELETE, UPDATE, DISABLE }
    private final Product product;
    private final ActionType actionType;

    public ProductCommand(Product product, ActionType actionType) {
        this.product = product;
        this.actionType = actionType;
    }

    @Override
    public void execute() {
        switch (actionType) {
            case DELETE:
                AuditLogManager.getInstance().addLog("Xóa sản phẩm: " + product.getProductId());
                break;
            case UPDATE:
                AuditLogManager.getInstance().addLog("Cập nhật sản phẩm: " + product.getProductId());
                break;
            case DISABLE:
                AuditLogManager.getInstance().addLog("Vô hiệu hóa sản phẩm: " + product.getProductId());
                break;
        }
    }

    @Override
    public String getDescription() {
        switch (actionType) {
            case DELETE: return "Xóa sản phẩm: " + product.getProductId();
            case UPDATE: return "Cập nhật sản phẩm: " + product.getProductId();
            case DISABLE: return "Vô hiệu hóa sản phẩm: " + product.getProductId();
            default: return "";
        }
    }
}