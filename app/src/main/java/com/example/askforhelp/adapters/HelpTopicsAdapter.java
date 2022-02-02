package com.example.askforhelp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;

import java.util.ArrayList;

public class HelpTopicsAdapter extends RecyclerView.Adapter<HelpTopicsAdapter.HelpTopicViewHolder> {
    private ArrayList<HelpTopicItem> topicsList;
    private OnHelpTopicClickedListener helpTopicClickedListener;

    private final int LIKES_LIMIT = 1000;
    private final String LIKES_LIMIT_EXCEEDED_DISPLAY = "999+";

    public interface OnHelpTopicClickedListener {
        void onHelpTopicClicked(int position);
    }

    public void setOnHelpTopicClickedListener(OnHelpTopicClickedListener listener) {
        helpTopicClickedListener = listener;
    }

    public static class HelpTopicViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView likes;
        public TextView author;

        public HelpTopicViewHolder(@NonNull View itemView, OnHelpTopicClickedListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.topics_view_fragment_topic_item_title);
            likes = itemView.findViewById(R.id.topics_view_fragment_topic_item_likes);
            author = itemView.findViewById(R.id.topics_view_fragment_topic_item_author);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onHelpTopicClicked(position);
                    }
                }
            });
        }
    }

    public HelpTopicsAdapter(ArrayList<HelpTopicItem> topicsList) {
        this.topicsList = topicsList;
    }

    @NonNull
    @Override
    public HelpTopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.topics_view_topic_item, parent, false);
        return new HelpTopicViewHolder(view, helpTopicClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpTopicViewHolder holder, int position) {
        HelpTopicItem currentItem = topicsList.get(position);

        holder.title.setText(currentItem.getTitle());
        holder.author.setText(currentItem.getAuthorName());
        if (currentItem.getLikes()<LIKES_LIMIT) {
            holder.likes.setText(String.valueOf(currentItem.getLikes()));
        } else {
            holder.likes.setText(LIKES_LIMIT_EXCEEDED_DISPLAY);
        }
    }

    @Override
    public int getItemCount() {
        return topicsList.size();
    }
}
