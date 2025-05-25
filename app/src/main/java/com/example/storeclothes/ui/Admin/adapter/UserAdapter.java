package com.example.storeclothes.ui.Admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private final Consumer<String> onDeleteClickListener;

    public UserAdapter(List<User> users, Consumer<String> onDeleteClickListener) {
        this.users = users != null ? users : new ArrayList<>();
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateList(List<User> newUsers) {
        this.users = newUsers != null ? newUsers : new ArrayList<>();
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvUserEmail;
        private final TextView tvUserJoinDate;
        private final TextView tvUserStatus;
        private final ImageButton btnDeleteUser;
        private final ImageButton btnToggleUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserJoinDate = itemView.findViewById(R.id.tvUserJoinDate);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
            btnToggleUser = itemView.findViewById(R.id.btnToggleUser);
        }

        public void bind(User user) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            tvUserName.setText(fullName);
            tvUserEmail.setText(user.getEmail());
            
            // Hiển thị ngày tham gia (sử dụng birthdate tạm thời)
            if (user.getBirthdate() != null) {
                tvUserJoinDate.setText("Join Date: " + user.getBirthdate().toString());
            } else {
                tvUserJoinDate.setText("Join Date: N/A");
            }
            
            // Hiển thị trạng thái người dùng
            boolean isVerified = user.getIsVerified() != null ? user.getIsVerified() : false;
            tvUserStatus.setText(isVerified ? "Active" : "Inactive");
            tvUserStatus.setBackgroundResource(isVerified ? 
                    R.drawable.status_badge_active : R.drawable.status_badge_inactive);
            
            // Thiết lập sự kiện xóa người dùng
            btnDeleteUser.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.accept(user.getUserId());
                }
            });
            
            // Thiết lập sự kiện toggle trạng thái người dùng (có thể triển khai sau)
            btnToggleUser.setOnClickListener(v -> {
                // Xử lý toggle trạng thái người dùng
            });
        }
    }
}