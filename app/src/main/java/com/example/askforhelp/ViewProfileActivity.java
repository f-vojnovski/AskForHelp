package com.example.askforhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.askforhelp.adapters.HelpTopicSolutionsAdapter;
import com.example.askforhelp.adapters.TopicSolutionItem;
import com.example.askforhelp.adapters.UserActivityAdapter;
import com.example.askforhelp.adapters.UserActivityLogItem;
import com.example.askforhelp.adapters.ViewHelpTopicImageItem;
import com.example.askforhelp.adapters.ViewHelpTopicImagesAdapter;
import com.example.askforhelp.datainterface.UserActivityType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView userDisplayNameTextView;

    private String userUid;

    private RecyclerView userActivityRecyclerView;
    private UserActivityAdapter userActivityAdapter;
    private RecyclerView.LayoutManager userActivityLayoutManager;
    private ArrayList<UserActivityLogItem> userLogsList;

    private DatabaseReference mDatabaseRef;

    ValueEventListener activityLogsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            userLogsList.clear();
            for (DataSnapshot logsSnapshot : snapshot.getChildren()) {
                String topicUid = (String) logsSnapshot.child("topicUid").getValue();
                String topicTitle = (String) logsSnapshot.child("topicTitle").getValue();
                String activityTypeString = (String) logsSnapshot.child("activityType").getValue();

                UserActivityType activityType;
                if (activityTypeString.equals("ADDED_TOPIC")) {
                    activityType = UserActivityType.ADDED_TOPIC;
                }
                else {
                    activityType = UserActivityType.ADDED_SOLUTION;
                }

                UserActivityLogItem logItem = new UserActivityLogItem(topicUid, topicTitle, activityType);
                userLogsList.add(logItem);
            }
            userActivityAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // TODO: Display error if post can't be read
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Bundle extras = getIntent().getExtras();
        userUid = extras.getString("USER_UID");

        if (userUid == null) {
            return;
        }

        userDisplayNameTextView = findViewById(R.id.view_profile_activity_user_display_name);
        userDisplayNameTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        userLogsList = new ArrayList<>();

        userActivityRecyclerView = findViewById(R.id.view_profile_activity_logs_recycler_view);
        userActivityRecyclerView.setHasFixedSize(true);
        userActivityLayoutManager = new LinearLayoutManager(this);
        userActivityAdapter = new UserActivityAdapter(userLogsList);
        userActivityRecyclerView.setLayoutManager(userActivityLayoutManager);
        userActivityRecyclerView.setAdapter(userActivityAdapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        Query logsQuery = mDatabaseRef
                .child("users-activity-logs")
                .child(userUid)
                .orderByChild("dateTime");

        logsQuery.addValueEventListener(activityLogsListener);
    }
}