package com.example.askforhelp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.askforhelp.adapters.VendorItem;
import com.example.askforhelp.adapters.VendorsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VendorsFragment extends Fragment {
    private RecyclerView vendorsRecyclerView;
    private VendorsAdapter vendorsAdapter;
    private RecyclerView.LayoutManager vendorItemsLayoutManager;
    private ArrayList<VendorItem> vendorsList;

    private DatabaseReference mDatabaseRef;

    ValueEventListener vendorsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot vendorSnapshot : dataSnapshot.getChildren()) {
                String uid = vendorSnapshot.getKey();
                String name = (String) vendorSnapshot.child("name").getValue();
                String description = (String) vendorSnapshot.child("description").getValue();
                String location = (String) vendorSnapshot.child("location").getValue();

                VendorItem vendorItem = new VendorItem(name, description, location);
                vendorsList.add(vendorItem);
                vendorsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO: Display error
        }
    };

    public VendorsFragment() {
        // Required empty public constructor
    }

    public static VendorsFragment newInstance() {
        VendorsFragment fragment = new VendorsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vendors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        vendorsList = new ArrayList<>();

        vendorsRecyclerView = view.findViewById(R.id.vendors_view_fragment_vendors_recycler_view);
        vendorsRecyclerView.setHasFixedSize(true);
        vendorItemsLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        vendorsAdapter = new VendorsAdapter(vendorsList);
        vendorsRecyclerView.setLayoutManager(vendorItemsLayoutManager);
        vendorsRecyclerView.setAdapter(vendorsAdapter);

        vendorsAdapter.setOnItemClickListener(position -> {
            openVendorLocation(vendorsList.get(position));
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("vendors").addValueEventListener(vendorsListener);
    }

    private void openVendorLocation(VendorItem vendor) {
        Uri gmmIntentUri = Uri.parse(vendor.getLocation());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}