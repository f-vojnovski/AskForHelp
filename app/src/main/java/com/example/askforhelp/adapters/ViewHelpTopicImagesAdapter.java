package com.example.askforhelp.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewHelpTopicImagesAdapter extends
        RecyclerView.Adapter<ViewHelpTopicImagesAdapter.ViewHelpTopicImageViewHolder>{
    public static class ViewHelpTopicImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHelpTopicImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.view_help_topic_image_item_image);
        }
    }

    private ArrayList<ViewHelpTopicImageItem> imageList;

    public ViewHelpTopicImagesAdapter(ArrayList<ViewHelpTopicImageItem> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHelpTopicImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_help_topic_image_item, parent, false);
        return new ViewHelpTopicImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHelpTopicImageViewHolder holder, int position) {
        ViewHelpTopicImageItem currentItem = imageList.get(position);

        Picasso.get()
                .load(currentItem.getImageUrl())
                .placeholder(R.drawable.ic_baseline_image_24)
                .fit()
                .centerCrop()
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
