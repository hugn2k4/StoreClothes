package com.example.storeclothes.ui.Strategy;

import android.app.Dialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.factory.EntityFactory;

public class ProductActionStrategy implements EntityActionStrategy {
    private final Context context;
    private final EntityFactory productFactory;

    public ProductActionStrategy(Context context) {
        this.context = context;
        this.productFactory = new EntityFactory.ProductFactory();
    }

    @Override
    public void add(Entity entity) {
        DataStore.getInstance().getProducts().add(entity);
        Toast.makeText(context, "Product added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void edit(Entity entity) {
        Product product = (Product) entity;
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_product);

        EditText etName = dialog.findViewById(R.id.etProductName);
        EditText etDescription = dialog.findViewById(R.id.etProductDescription);
        EditText etPrice = dialog.findViewById(R.id.etProductPrice);
        EditText etCategoryId = dialog.findViewById(R.id.etProductCategory);
        etName.setText(product.getName());
        etDescription.setText(product.getDescription());
        etPrice.setText(String.valueOf(product.getPrice()));
        etCategoryId.setText(product.getCategoryId());

        dialog.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etName.getText().toString();
            String description = etDescription.getText().toString();
            String priceStr = etPrice.getText().toString();
            String categoryId = etCategoryId.getText().toString();
            if (!name.isEmpty() && !description.isEmpty() && !priceStr.isEmpty() && !categoryId.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);
                    product.setName(name);
                    product.setDescription(description);
                    product.setPrice(price);
                    product.setCategoryId(categoryId);
                    Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void delete(Entity entity) {
        DataStore.getInstance().getProducts().remove(entity);
        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toggleStatus(Entity entity) {
        // Không áp dụng cho sản phẩm
    }
}