package com.example.askforhelp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;

import java.util.ArrayList;

public class AddNewTopicImagesAdapter
        extends RecyclerView.Adapter<AddNewTopicImagesAdapter.AddNewTopicImageItemViewHolder> {
    private ArrayList<AddNewTopicImageItem> mImageList;

    // TODO: Rename this class
    public static class AddNewTopicImageItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public AddNewTopicImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.add_new_topic_image_list_image);
        }
    }

    public AddNewTopicImagesAdapter(ArrayList<AddNewTopicImageItem> imageList) {
        this.mImageList = imageList;
    }

    @NonNull
    @Override
    public AddNewTopicImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.add_new_topic_image_list_item, parent, false);
        return new AddNewTopicImageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddNewTopicImageItemViewHolder holder, int position) {
        AddNewTopicImageItem currentItem = mImageList.get(position);

        holder.mImageView.setImageBitmap(currentItem.getmImageResource());
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }
}
