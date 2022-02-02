package com.example.askforhelp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HandymenAdapter extends RecyclerView.Adapter<HandymenAdapter.HandymanViewHolder> {
    public static class HandymanViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView biography;
        public TextView distance;
        public ImageButton messageHandymanButton;

        public HandymanViewHolder(@NonNull View itemView,
                                  final HandymenAdapter.OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.services_view_handyman_item_handyman_name);
            biography = itemView.findViewById(R.id.services_view_handyman_item_handyman_biography);
            distance = itemView.findViewById(R.id.services_view_handyman_item_handyman_distance);
            messageHandymanButton =
                    itemView.findViewById(R.id.services_view_handyman_item_message_button);

            messageHandymanButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMessageHandymanClick(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onMessageHandymanClick(int position);
    }

    private ArrayList<HandymanItem> handymenList;
    private HandymenAdapter.OnItemClickListener onItemClickListener;

    public HandymenAdapter(ArrayList<HandymanItem> handymenList) {
        this.handymenList = handymenList;
    }

    @NonNull
    @Override
    public HandymenAdapter.HandymanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.services_view_handyman_item, parent, false);
        return new HandymenAdapter.HandymanViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HandymanViewHolder holder, int position) {
        HandymanItem currentItem = handymenList.get(position);

        holder.name.setText(currentItem.getHandymanName());
        holder.biography.setText(currentItem.getBiograhy());
        holder.distance.setText
                (new DecimalFormat("#.0km")
                .format(currentItem.getDistance()));
    }

    @Override
    public int getItemCount() {
        return handymenList.size();
    }

    public void setOnItemClickListener(HandymenAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
