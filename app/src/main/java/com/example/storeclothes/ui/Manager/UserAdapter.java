package com.example.storeclothes.ui.Manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private final List<User> users;
    private OnUserClickListener listener;
    private OnUserActionListener actionListener;

    public interface OnUserClickListener {
        void onUserClick(User user, int position);
    }
    
    public interface OnUserActionListener {
        void onDeleteUser(User user, int position);
        void onToggleUserStatus(User user, int position);
    }

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnUserActionListener(OnUserActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        
        // Hiển thị tên đầy đủ của người dùng
        String fullName = (user.getLastName() != null ? user.getLastName() : "") + " " + 
                         (user.getFirstName() != null ? user.getFirstName() : "");
        holder.tvUserName.setText(fullName.trim());
        
        // Hiển thị email
        holder.tvUserEmail.setText(user.getEmail());
        
        // Hiển thị số điện thoại nếu có
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            holder.tvUserPhone.setVisibility(View.VISIBLE);
            holder.tvUserPhone.setText(user.getPhone());
        } else {
            holder.tvUserPhone.setVisibility(View.GONE);
        }
        
        // Hiển thị trạng thái người dùng
        String status = user.getStatus();
        if (status != null) {
            if (status.equals("active")) {
                holder.tvUserStatus.setText("Hoạt động");
                holder.tvUserStatus.setTextColor(ContextCompat.getColor(context, R.color.teal_700));
                holder.btnToggleStatus.setText("Vô hiệu hóa");
            } else if (status.equals("disabled")) {
                holder.tvUserStatus.setText("Đã vô hiệu hóa");
                holder.tvUserStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.btnToggleStatus.setText("Hủy vô hiệu hóa");
            }
        } else {
            holder.tvUserStatus.setText("Hoạt động");
            holder.tvUserStatus.setTextColor(ContextCompat.getColor(context, R.color.teal_700));
            holder.btnToggleStatus.setText("Vô hiệu hóa");
        }
        
        // Hiển thị avatar người dùng
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_loading)
                    .into(holder.imgUserAvatar);
        } else {
            // Sử dụng avatar mặc định với chữ cái đầu tiên của tên
            holder.imgUserAvatar.setImageResource(R.drawable.ic_loading);
            
            // Đặt màu nền ngẫu nhiên cho avatar
            int[] avatarColors = context.getResources().getIntArray(R.array.avatar_colors);
            int colorIndex = Math.abs(fullName.hashCode()) % avatarColors.length;
            holder.imgUserAvatar.setBackgroundColor(avatarColors[colorIndex]);
        }
        
        // Xử lý sự kiện click
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user, position);
            }
        });
        
        // Xử lý sự kiện click nút xóa
        holder.btnDeleteUser.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteUser(user, position);
            }
        });
        
        // Xử lý sự kiện click nút vô hiệu hóa/hủy vô hiệu hóa
        holder.btnToggleStatus.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onToggleUserStatus(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvUserName, tvUserEmail, tvUserPhone, tvUserStatus;
        ImageView imgUserAvatar;
        Button btnToggleStatus, btnDeleteUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}