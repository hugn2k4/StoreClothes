package com.example.storeclothes.ui.Manager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.data.repository.UserRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerViewUsers;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvEmptyUsers;
    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;
    private UserRepository userRepository;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo repository
        userRepository = UserRepository.getInstance();
        
        // Khởi tạo views
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvEmptyUsers = view.findViewById(R.id.tvEmptyUsers);
        tvEmptyUsers.setText("Không có người dùng nào");
        
        setupRecyclerView();
        setupSwipeRefresh();
        observeCustomers();
    }

    private void setupRecyclerView() {
        // Sử dụng GridLayoutManager với 2 cột để hiển thị người dùng dạng lưới
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewUsers.setLayoutManager(layoutManager);
        
        userAdapter = new UserAdapter(getContext(), userList);
        recyclerViewUsers.setAdapter(userAdapter);
        
        // Thiết lập listener cho các hành động trên người dùng
        userAdapter.setOnUserActionListener(new UserAdapter.OnUserActionListener() {
            @Override
            public void onDeleteUser(User user, int position) {
                showDeleteConfirmationDialog(user);
            }

            @Override
            public void onToggleUserStatus(User user, int position) {
                String newStatus = user.getStatus() != null && user.getStatus().equals("active") ? 
                        "disabled" : "active";
                showToggleStatusConfirmationDialog(user, newStatus);
            }
        });
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.purple_500, R.color.teal_200);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            userRepository.refreshCustomers();
        });
    }
    
    private void observeCustomers() {
        // Quan sát trạng thái loading
        userRepository.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            showLoading(isLoading);
            if (!isLoading) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        
        // Quan sát danh sách người dùng
        userRepository.getCustomers().observe(getViewLifecycleOwner(), users -> {
            userList.clear();
            userList.addAll(users);
            userAdapter.notifyDataSetChanged();
            
            // Hiển thị thông báo nếu không có người dùng
            if (users.isEmpty()) {
                tvEmptyUsers.setVisibility(View.VISIBLE);
            } else {
                tvEmptyUsers.setVisibility(View.GONE);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
    
    // Hiển thị dialog xác nhận xóa người dùng
    private void showDeleteConfirmationDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng này không? Hành động này không thể hoàn tác.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteUser(user);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    
    // Hiển thị dialog xác nhận thay đổi trạng thái người dùng
    private void showToggleStatusConfirmationDialog(User user, String newStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String title = newStatus.equals("active") ? "Xác nhận kích hoạt" : "Xác nhận vô hiệu hóa";
        String message = newStatus.equals("active") ? 
                "Bạn có chắc chắn muốn kích hoạt người dùng này không?" : 
                "Bạn có chắc chắn muốn vô hiệu hóa người dùng này không?";
        
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            updateUserStatus(user, newStatus);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    
    // Xóa người dùng
    private void deleteUser(User user) {
        userRepository.deleteUser(user, new UserRepository.OnUserActionListener() {
            @Override
            public void onSuccess(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    // Cập nhật trạng thái người dùng
    private void updateUserStatus(User user, String newStatus) {
        userRepository.updateUserStatus(user, newStatus, new UserRepository.OnUserActionListener() {
            @Override
            public void onSuccess(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}