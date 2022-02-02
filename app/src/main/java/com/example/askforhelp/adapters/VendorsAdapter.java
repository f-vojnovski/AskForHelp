package com.example.askforhelp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.askforhelp.R;

import java.util.ArrayList;

public class VendorsAdapter extends RecyclerView.Adapter<VendorsAdapter.VendorViewHolder> {
    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public ImageButton viewLocationButton;

        public VendorViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.vendors_view_item_vendor_name);
            description = itemView.findViewById(R.id.vendors_view_item_vendor_description);
            viewLocationButton = itemView.findViewById(R.id.vendors_view_item_vendor_location_button);

            viewLocationButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onLocationClick(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onLocationClick(int position);
    }

    private ArrayList<VendorItem> vendorsList;
    private OnItemClickListener onItemClickListener;

    public VendorsAdapter(ArrayList<VendorItem> vendorsList) {
        this.vendorsList = vendorsList;
    }

    @NonNull
    @Override
    public VendorsAdapter.VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_vendors_vendor_item, parent, false);
        return new VendorsAdapter.VendorViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorsAdapter.VendorViewHolder holder, int position) {
        VendorItem currentItem = vendorsList.get(position);

        holder.name.setText(currentItem.getVendorName());
        holder.description.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return vendorsList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
