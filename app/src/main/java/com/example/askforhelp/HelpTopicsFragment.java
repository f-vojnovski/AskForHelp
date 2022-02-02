package com.example.askforhelp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.askforhelp.adapters.HelpTopicsAdapter;
import com.example.askforhelp.adapters.HelpTopicItem;
import com.example.askforhelp.firebase.model.HelpTopic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HelpTopicsFragment extends Fragment {
    private RecyclerView topicsRecyclerView;
    private HelpTopicsAdapter topicsAdapter;
    private RecyclerView.LayoutManager topicsLayoutManager;
    private ArrayList<HelpTopicItem> topicsList;

    private DatabaseReference mDatabaseRef;

    ValueEventListener helpTopicsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            topicsList.clear();
            for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                String uid = topicSnapshot.getKey();
                String title = (String) topicSnapshot.child("title").getValue();
                Long likes = (Long) topicSnapshot.child("likes").getValue();
                String authorUid = (String) topicSnapshot.child("authorUid").getValue();
                String authorName = (String) topicSnapshot.child("authorName").getValue();
                HelpTopic.HelpTopicStatus status = HelpTopic.HelpTopicStatus.NOT_RESOLVED;

                HelpTopicItem topicItem =
                        new HelpTopicItem(uid, title, status, likes, authorUid, authorName);
                topicsList.add(topicItem);
                topicsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO: Display error
        }
    };

    public HelpTopicsFragment() {
        // Required empty public constructor
    }

    public static HelpTopicsFragment newInstance() {
        HelpTopicsFragment fragment = new HelpTopicsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_topics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        topicsList = new ArrayList<>();

        topicsRecyclerView = view.findViewById(R.id.help_topics_fragment_topics_recycler_view);
        topicsRecyclerView.setHasFixedSize(true);
        topicsLayoutManager = new LinearLayoutManager(getActivity());
        topicsAdapter = new HelpTopicsAdapter(topicsList);
        topicsRecyclerView.setLayoutManager(topicsLayoutManager);
        topicsRecyclerView.setAdapter(topicsAdapter);

        topicsAdapter.setOnHelpTopicClickedListener(position -> {
            onTopicSelected(topicsList.get(position).getUid());

        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabaseRef.child("help-topics").orderByChild("dateTime");
        query.addValueEventListener(helpTopicsListener);
    }

    private void onTopicSelected(String topicUid) {
        Intent intent = new Intent(getActivity(), ViewHelpTopicActivity.class);
        intent.putExtra("HELP_TOPIC_UID", topicUid);
        startActivity(intent);
    }
}