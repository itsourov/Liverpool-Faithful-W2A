package com.liverpoolfaithful.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.liverpoolfaithful.app.R;
import com.liverpoolfaithful.app.fragment.CommentListFragment;
import com.liverpoolfaithful.app.fragment.CommentReplyFragment;
import com.liverpoolfaithful.app.helper.Configs;
import com.liverpoolfaithful.app.helper.MasterSourov;
import com.liverpoolfaithful.app.model.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comment> allComment;
    Context context;
    public boolean showShimmer = true;
    MasterSourov sourov;
    Activity activity;

    public CommentsAdapter(List<Comment> allComment, Context context) {
        this.allComment = allComment;
        this.context = context;
        activity = (Activity) context;
        sourov = new MasterSourov(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (showShimmer) {
            holder.shimmerFrameLayout.startShimmer();
        } else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);


            holder.author_name.setText(allComment.get(position).getAuthor_name());
            holder.date_text.setText(sourov.convertToAGo(allComment.get(position).getDate()));
            int replyCount = allComment.get(position).getChildNumber();
            if (replyCount > 0) {
                if (replyCount > 1) {
                    holder.add_a_reply_text.setText("view " + replyCount + " Replies");
                } else {
                    holder.add_a_reply_text.setText("view " + replyCount + " Reply");
                }

            } else {
                holder.add_a_reply_text.setText("Add a Reply");
            }


            Glide.with(context).load(allComment.get(position).getAuthor_avatar_urls()).placeholder(R.drawable.image_placeholder).into(holder.author_img);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.comment_text.setText(Html.fromHtml(allComment.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.comment_text.setText(Html.fromHtml(allComment.get(position).getContent()));
            }


            if (Configs.replyInCommentEnabled) {
                holder.add_a_reply_text.setOnClickListener(v -> {
                    CommentReplyFragment fragment = new CommentReplyFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("parent_id",  allComment.get(position).getComment_id());
                    fragment.setArguments(bundle);
                 fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "TAG");
                });
            } else {
                holder.add_a_reply_text.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        int shimmerItemNumber = 5;

        return showShimmer ? shimmerItemNumber : allComment.size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;
        TextView author_name, comment_text, date_text, add_a_reply_text;
        CircleImageView author_img;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            author_name = itemView.findViewById(R.id.author_name);
            comment_text = itemView.findViewById(R.id.comment_text);
            add_a_reply_text = itemView.findViewById(R.id.add_a_reply_text);
            date_text = itemView.findViewById(R.id.date_text);
            author_img = itemView.findViewById(R.id.author_img);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container);

        }
    }
}
