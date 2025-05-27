package com.example.storeclothes.ui.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.ui.Admin.adapter.ProductAdapter;
import com.example.storeclothes.ui.Admin.command.CommandInvoker;
import com.example.storeclothes.ui.Admin.command.DeleteProductCommand;
import com.example.storeclothes.ui.Admin.factory.ManagerFactory;
import com.example.storeclothes.ui.Admin.manager.BaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {
    private RecyclerView recyclerView;
    private BaseManager<Product> productManager;
    private Button btnUndo;
    private CommandInvoker commandInvoker;
    private static final int ADD_PRODUCT_REQUEST = 1001;
    private static final int EDIT_PRODUCT_REQUEST = 1002;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các thành phần
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        btnUndo = view.findViewById(R.id.btnUndo);
        FloatingActionButton fabAddProduct = view.findViewById(R.id.fabAddProduct);

        // Sử dụng Factory Pattern để tạo ProductManager
        productManager = ManagerFactory.createManager(ManagerFactory.ManagerType.PRODUCT);
        commandInvoker = CommandInvoker.getInstance();

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProductAdapter adapter = new ProductAdapter(new ArrayList<>(), this::onDeleteProduct);
        adapter.setOnItemClickListener(this::onEditProduct); // Thêm listener cho sự kiện click vào sản phẩm
        recyclerView.setAdapter(adapter);

        // Load dữ liệu sản phẩm
        loadProducts();

        // Thiết lập sự kiện
        btnUndo.setOnClickListener(v -> undoLastCommand());
        fabAddProduct.setOnClickListener(v -> {
            // Thêm log để kiểm tra
            Toast.makeText(getContext(), "Đang mở form thêm sản phẩm", Toast.LENGTH_SHORT).show();
            
            // Mở màn hình thêm sản phẩm
            Intent intent = new Intent(getActivity(), ProductFormActivity.class);
            startActivity(intent);
        });
    }

    // Thêm phương thức xử lý sự kiện click vào sản phẩm để chỉnh sửa
    private void onEditProduct(Product product) {
        Intent intent = new Intent(getActivity(), ProductFormActivity.class);
        intent.putExtra("productId", product.getProductId());
        intent.putExtra("categoryId", product.getCategoryId());
        startActivity(intent);
    }

    private void loadProducts() {
        productManager.getAll().observe(getViewLifecycleOwner(), products -> {
            // Cập nhật adapter với danh sách sản phẩm
            ((ProductAdapter) recyclerView.getAdapter()).updateList(products);
            updateUndoButtonState();
        });
    }

    private void onDeleteProduct(String productId) {
        // Sử dụng Command Pattern để xóa sản phẩm
        DeleteProductCommand deleteCommand = new DeleteProductCommand(productId);
        commandInvoker.executeCommand(deleteCommand);
        ProductAdapter adapter = (ProductAdapter) recyclerView.getAdapter();
        adapter.removeProductById(productId);
        Toast.makeText(getContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
        updateUndoButtonState();
    }
    
    private void undoLastCommand() {
        if (commandInvoker.canUndo()) {
            commandInvoker.undoLastCommand();
            Toast.makeText(getContext(), "Đã hoàn tác thao tác cuối cùng", Toast.LENGTH_SHORT).show();
            loadProducts(); // Tải lại dữ liệu
            updateUndoButtonState();
        }
    }

    private void updateUndoButtonState() {
        btnUndo.setVisibility(commandInvoker.canUndo() ? View.VISIBLE : View.GONE);
        btnUndo.setEnabled(commandInvoker.canUndo());
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == ADD_PRODUCT_REQUEST) {
                Toast.makeText(getContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                loadProducts();
            } else if (requestCode == EDIT_PRODUCT_REQUEST) {
                Toast.makeText(getContext(), "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                loadProducts();
            }
        }
    }
}