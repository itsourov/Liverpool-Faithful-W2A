package com.liverpoolfaithful.app.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.liverpoolfaithful.app.CategoryBasedPostList;
import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.model.Category;

import java.util.List;
import java.util.Random;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    public boolean showShimmer = true;
    List<Category> allCategory;
    Context context;

    public CategoryListAdapter(List<Category> allCategory, Context context) {
        this.allCategory = allCategory;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_cat_item, parent, false);

        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer();
        } else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);

            Glide.with(context)
                    .load(allCategory.get(position).getImageLink())
                    .placeholder(R.drawable.image_placeholder)
                    .centerCrop()
                    .error(R.mipmap.ic_launcher)
                    .into(holder.catImage);
            Random rand = new Random();
            int i = rand.nextInt(4) + 1;


            holder.category_name.setText(allCategory.get(position).getName());
            holder.countText.setText("(" + allCategory.get(position).getCount() + ")");


            // handle the click on post

            holder.itemView.setOnClickListener(v -> {


                Intent intent = new Intent(context, CategoryBasedPostList.class);
                intent.putExtra("catId", allCategory.get(position).getId());
                intent.putExtra("catName", allCategory.get(position).getName());
                context.startActivity(intent);


            });
        }

    }

    @Override
    public int getItemCount() {
        int shimmerItemNumber = 5;

        return showShimmer ? shimmerItemNumber : allCategory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView category_name, countText;
        ImageView catImage;
        ShimmerFrameLayout shimmerFrameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            category_name = itemView.findViewById(R.id.category_name);
            catImage = itemView.findViewById(R.id.catImage);
            countText = itemView.findViewById(R.id.countText);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container);


        }
    }
}