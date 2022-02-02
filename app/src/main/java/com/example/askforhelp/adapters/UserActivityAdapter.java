package com.example.askforhelp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;

import java.util.ArrayList;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.ActivityLogViewHolder>{
    private ArrayList<UserActivityLogItem> logsList;
    private OnActivityLogClickedListener helpTopicClickedListener;

    public interface OnActivityLogClickedListener {
        void onActivityLogClickedListener(int position);
    }

    public void setOnActivityLogClickedListener(OnActivityLogClickedListener listener) {
        helpTopicClickedListener = listener;
    }

    public static class ActivityLogViewHolder extends RecyclerView.ViewHolder {
        public TextView activityType;
        public TextView topicTitle;

        public ActivityLogViewHolder(@NonNull View itemView, OnActivityLogClickedListener listener) {
            super(itemView);
            activityType = itemView.findViewById(R.id.user_activity_log_item_type_label);
            topicTitle = itemView.findViewById(R.id.user_activity_log_item_topic_title);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onActivityLogClickedListener(position);
                    }
                }
            });
        }
    }

    public UserActivityAdapter(ArrayList<UserActivityLogItem> logsList) {
        this.logsList = logsList;
    }

    @NonNull
    @Override
    public UserActivityAdapter.ActivityLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.user_activity_item, parent, false);
        return new UserActivityAdapter.ActivityLogViewHolder(view, helpTopicClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserActivityAdapter.ActivityLogViewHolder holder, int position) {
        UserActivityLogItem currentItem = logsList.get(position);

        holder.topicTitle.setText(currentItem.getTopicTitle());
        switch (currentItem.getActivityType()) {
            case ADDED_SOLUTION:
                holder.activityType.setText(R.string.user_activity_adapter_posted_comment);
                break;
            case ADDED_TOPIC:
                holder.activityType.setText(R.string.user_activity_adapter_posted_topic);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return logsList.size();
    }
}
