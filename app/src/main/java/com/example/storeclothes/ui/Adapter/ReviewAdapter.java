package com.example.storeclothes.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storeclothes.R;
import com.example.storeclothes.data.model.Review;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Load avatar bằng Glide
        Glide.with(context)
                .load(review.getAvatarUrl())
                .placeholder(R.drawable.avata) // hình mặc định nếu chưa có avatar
                .into(holder.imgAvatar);

        holder.txtName.setText(review.getUserName());
        holder.ratingBar.setRating((float) review.getRating());
        holder.txtReview.setText(review.getComment());

        // Định dạng thời gian
        if (review.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.txtTime.setText(sdf.format(review.getDate()));
        } else {
            holder.txtTime.setText("Chưa rõ ngày");
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtReview, txtTime;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtUserName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtReview = itemView.findViewById(R.id.txtReviewContent);
            txtTime = itemView.findViewById(R.id.txtReviewTime);
        }
    }
}
