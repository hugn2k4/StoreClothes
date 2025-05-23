package com.example.storeclothes.ui.Manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Product;
import com.example.storeclothes.data.service.ProductService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private ProgressBar progressBar;
    private ProductManagerAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private FloatingActionButton fabAddProduct;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddProduct = getActivity().findViewById(R.id.fabAddProduct);
        
        setupRecyclerView();
        loadProducts();
        
        // Hiển thị FAB khi ở fragment sản phẩm
        if (fabAddProduct != null) {
            fabAddProduct.setVisibility(View.VISIBLE);
        }
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductManagerAdapter(getContext(), productList, new ProductManagerAdapter.OnProductActionListener() {
            @Override
            public void onEditClick(Product product) {
                ((ManagerActivity) getActivity()).showEditProductDialog(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                ((ManagerActivity) getActivity()).showDeleteConfirmationDialog(product);
            }

            @Override
            public void onItemClick(Product product) {
                // Hiển thị chi tiết sản phẩm nếu cần
                Toast.makeText(getContext(), "Đã chọn: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewProducts.setAdapter(productAdapter);
    }

    public void loadProducts() {
        showLoading(true);
        ProductService.getInstance().getProductListFromFirebase(new ProductService.OnProductListListener() {
            @Override
            public void onSuccess(List<Product> products) {
                showLoading(false);
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hiển thị FAB khi ở fragment sản phẩm
        if (fabAddProduct != null) {
            fabAddProduct.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Ẩn FAB khi rời khỏi fragment sản phẩm
        if (fabAddProduct != null) {
            fabAddProduct.setVisibility(View.GONE);
        }
    }
}