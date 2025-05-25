package com.example.storeclothes.ui.Admin;

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
import com.example.storeclothes.data.model.User;
import com.example.storeclothes.ui.Admin.adapter.UserAdapter;
import com.example.storeclothes.ui.Admin.command.CommandInvoker;
import com.example.storeclothes.ui.Admin.command.DeleteUserCommand;
import com.example.storeclothes.ui.Admin.factory.ManagerFactory;
import com.example.storeclothes.ui.Admin.manager.BaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private BaseManager<User> userManager;
    private Button btnUndo;
    private CommandInvoker commandInvoker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các thành phần
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        btnUndo = view.findViewById(R.id.btnUndo);
        FloatingActionButton fabAddUser = view.findViewById(R.id.fabAddUser);

        // Sử dụng Factory Pattern để tạo UserManager
        userManager = ManagerFactory.createManager(ManagerFactory.ManagerType.USER);
        commandInvoker = CommandInvoker.getInstance();

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        UserAdapter adapter = new UserAdapter(new ArrayList<>(), this::onDeleteUser);
        recyclerView.setAdapter(adapter);

        // Load dữ liệu người dùng
        loadUsers();

        // Thiết lập sự kiện
        btnUndo.setOnClickListener(v -> undoLastCommand());
        fabAddUser.setOnClickListener(v -> {
            // Mở màn hình thêm người dùng
        });
    }

    private void loadUsers() {
        userManager.getAll().observe(getViewLifecycleOwner(), users -> {
            // Cập nhật adapter với danh sách người dùng
            ((UserAdapter) recyclerView.getAdapter()).updateList(users);
            updateUndoButtonState();
        });
    }

    private void onDeleteUser(String userId) {
        // Sử dụng Command Pattern để xóa người dùng
        DeleteUserCommand deleteCommand = new DeleteUserCommand(userId);
        commandInvoker.executeCommand(deleteCommand);
        Toast.makeText(getContext(), "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
        updateUndoButtonState();
    }

    private void undoLastCommand() {
        if (commandInvoker.canUndo()) {
            commandInvoker.undoLastCommand();
            Toast.makeText(getContext(), "Đã hoàn tác thao tác cuối cùng", Toast.LENGTH_SHORT).show();
            loadUsers(); // Tải lại dữ liệu
            updateUndoButtonState();
        }
    }

    private void updateUndoButtonState() {
        btnUndo.setEnabled(commandInvoker.canUndo());
    }
}