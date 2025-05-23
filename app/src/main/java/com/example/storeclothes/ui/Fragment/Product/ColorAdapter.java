package com.example.storeclothes.ui.Fragment.Product;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;
import com.example.storeclothes.data.model.ColorItem;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private final List<ColorItem> colorList;
    private final String selectedColorName;
    private final OnColorSelectedListener listener;
    private final Context context;

    public interface OnColorSelectedListener {
        void onColorSelected(String colorName);
    }

    public ColorAdapter(Context context, List<ColorItem> colorList, String selectedColorName, OnColorSelectedListener listener) {
        this.context = context;
        this.colorList = colorList;
        this.selectedColorName = selectedColorName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorItem colorItem = colorList.get(position);
        holder.tvColorName.setText(colorItem.getName());

        // Lấy màu thực từ resource và set background hoặc gì đó cho view hiển thị màu
        int colorInt = ContextCompat.getColor(context, colorItem.getColorInt());
        GradientDrawable bgShape = (GradientDrawable) holder.colorPreview.getBackground();
        bgShape.setColor(colorInt);

        // Hiển thị check nếu tên màu trùng với selectedColorName
        holder.imageCheck.setVisibility(colorItem.getName().equals(selectedColorName) ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(v -> {
            listener.onColorSelected(colorItem.getName());
            // Nếu muốn update chọn lại check, có thể notify lại adapter ở bên ngoài
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        TextView tvColorName;
        View colorPreview;
        ImageView imageCheck;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvColorName = itemView.findViewById(R.id.tvColorName);
            colorPreview = itemView.findViewById(R.id.colorPreview);
            imageCheck = itemView.findViewById(R.id.imageCheck);
        }
    }
}
