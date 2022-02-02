package com.example.askforhelp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;

import java.util.ArrayList;

public class HelpTopicSolutionsAdapter extends RecyclerView.Adapter<HelpTopicSolutionsAdapter.TopicSolutionViewHolder> {
    private ArrayList<TopicSolutionItem> solutionsList;

    private final int LIKES_LIMIT = 1000;
    private final String LIKES_LIMIT_EXCEEDED_DISPLAY = "999+";

    public static class TopicSolutionViewHolder extends RecyclerView.ViewHolder {
        public TextView body;
        public TextView likes;
        public TextView author;

        public TopicSolutionViewHolder(@NonNull View itemView) {
            super(itemView);
            body = itemView.findViewById(R.id.view_help_topic_solution_item_body);
            likes = itemView.findViewById(R.id.view_help_topic_solution_item_likes);
            author = itemView.findViewById(R.id.view_help_topic_solution_item_author);
        }
    }

    public HelpTopicSolutionsAdapter(ArrayList<TopicSolutionItem> solutionsList) {
        this.solutionsList = solutionsList;
    }

    @NonNull
    @Override
    public HelpTopicSolutionsAdapter.TopicSolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_help_topic_solution_item, parent, false);
        return new TopicSolutionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicSolutionViewHolder holder, int position) {
        TopicSolutionItem currentItem = solutionsList.get(position);

        holder.body.setText(currentItem.getBody());
        holder.author.setText(currentItem.getAuthorName());
        if (currentItem.getLikes()<LIKES_LIMIT) {
            holder.likes.setText(String.valueOf(currentItem.getLikes()));
        } else {
            holder.likes.setText(LIKES_LIMIT_EXCEEDED_DISPLAY);
        }
    }

    @Override
    public int getItemCount() {
        return solutionsList.size();
    }
}
