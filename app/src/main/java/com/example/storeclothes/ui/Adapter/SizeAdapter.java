package com.example.storeclothes.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storeclothes.R;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private final Context context;
    private final List<String> sizeList;
    private final String selectedSize;
    private final OnSizeSelectedListener listener;
    public interface OnSizeSelectedListener {
        void onSizeSelectedListener(String sizeName);
    }

    public SizeAdapter(Context context, List<String> sizeList, String selectedSize, OnSizeSelectedListener listener) {
        this.context = context;
        this.sizeList = sizeList;
        this.selectedSize = selectedSize;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_size, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String sizeName = sizeList.get(position);
        holder.tvSizeName.setText(sizeName);

        if (sizeName.equals(selectedSize)) {
            holder.imageCheck.setVisibility(View.VISIBLE);
        } else {
            holder.imageCheck.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onSizeSelectedListener(sizeName);
        });
    }

    @Override
    public int getItemCount() {
        return sizeList.size();
    }

    static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSizeName;
        ImageView imageCheck;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSizeName = itemView.findViewById(R.id.tvSizeName);
            imageCheck = itemView.findViewById(R.id.imageCheck);
        }
    }
}

