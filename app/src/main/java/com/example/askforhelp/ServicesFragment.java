package com.example.askforhelp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.askforhelp.adapters.HandymanItem;
import com.example.askforhelp.adapters.HandymenAdapter;
import com.example.askforhelp.util.LocationDistanceCalculator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServicesFragment extends Fragment {
    private RecyclerView handymenRecyclerView;
    private HandymenAdapter handymenAdapter;
    private RecyclerView.LayoutManager handymenItemsLayoutManager;
    private ArrayList<HandymanItem> handymenList;

    private FusedLocationProviderClient fusedLocationClient;
    private double clientLatitude;
    private double clientLongitude;

    private DatabaseReference mDatabaseRef;

    ProgressBar progressBar;

    ValueEventListener handymenListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            handymenList.clear();
            for (DataSnapshot handymanSnapshot : dataSnapshot.getChildren()) {
                String uid = handymanSnapshot.getKey();
                String name = (String) handymanSnapshot.child("name").getValue();
                String biography = (String) handymanSnapshot.child("biography").getValue();
                double latitude = (double) handymanSnapshot.child("latitude").getValue();
                double longitude = (double) handymanSnapshot.child("longitude").getValue();

                float distance = (float) (LocationDistanceCalculator.findDistance
                        (latitude, clientLatitude,
                        longitude, clientLongitude) / 1000);


                HandymanItem handymanItem = new HandymanItem(uid, name, biography, distance);
                handymenList.add(handymanItem);
            }
            handymenAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO: Display error
        }
    };


    public ServicesFragment() {
        // Required empty public constructor
    }

    public static ServicesFragment newInstance() {
        ServicesFragment fragment = new ServicesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        handymenList = new ArrayList<>();

        handymenRecyclerView = view.findViewById(R.id.services_view_fragment_handymen_adapter);
        handymenRecyclerView.setHasFixedSize(true);
        handymenItemsLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        handymenAdapter = new HandymenAdapter(handymenList);
        handymenRecyclerView.setLayoutManager(handymenItemsLayoutManager);
        handymenRecyclerView.setAdapter(handymenAdapter);

        handymenAdapter.setOnItemClickListener(position -> {
            onMessageHandymanClicked(handymenList.get(position));
        });

        progressBar = view.findViewById(R.id.services_view_fragment_progress_bar);

        setupLocationClient();
    }

    private void setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat
                .checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        onLocationPermissionsGranted();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    onLocationPermissionsGranted();
                }
            });

    @SuppressLint("MissingPermission")
    private void onLocationPermissionsGranted() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    onLocationFound(location);
                }
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());
    }

    private void onLocationFound(Location location) {
        clientLatitude = location.getLatitude();
        clientLongitude = location.getLongitude();
        readHandymenData();
    }

    private void readHandymenData() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("handymen").addValueEventListener(handymenListener);
    }

    private void onMessageHandymanClicked(HandymanItem handyman) {
        // TODO: This will be implemented after messages are implemented
    }
}