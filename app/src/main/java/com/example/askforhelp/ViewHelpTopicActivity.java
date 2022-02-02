package com.example.askforhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.askforhelp.adapters.HelpTopicSolutionsAdapter;
import com.example.askforhelp.adapters.TopicSolutionItem;
import com.example.askforhelp.adapters.ViewHelpTopicImageItem;
import com.example.askforhelp.adapters.ViewHelpTopicImagesAdapter;
import com.example.askforhelp.datainterface.UserActivityType;
import com.example.askforhelp.firebase.model.HelpTopic;
import com.example.askforhelp.firebase.model.HelpTopicSolution;
import com.example.askforhelp.firebase.model.UserActivityLog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

public class ViewHelpTopicActivity extends AppCompatActivity {
    String helpTopicUid;

    private TextView topicTitleTextView;
    private TextView topicDescriptionTextView;
    private TextView topicLikesTextView;
    private TextView imagesLabelTextView;
    private TextInputEditText solutionEditText;
    private Button postSolutionButton;

    private RecyclerView topicImagesRecyclerView;
    private ViewHelpTopicImagesAdapter topicImagesAdapter;
    private RecyclerView.LayoutManager topicImagesLayoutManager;
    private ArrayList<ViewHelpTopicImageItem> topicImagesList;

    private RecyclerView topicSolutionsRecyclerView;
    private HelpTopicSolutionsAdapter topicSolutionsAdapter;
    private RecyclerView.LayoutManager topicSolutionsLayoutManager;
    private ArrayList<TopicSolutionItem> topicSolutionsList;

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    private String currentTopicTitle;
    private String currentTopicUid;

    ValueEventListener helpTopicListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            currentTopicUid = snapshot.getKey();
            currentTopicTitle = (String) snapshot.child("title").getValue();
            String description = (String) snapshot.child("description").getValue();
            Long likes = (Long) snapshot.child("likes").getValue();
            HelpTopic.HelpTopicStatus status = HelpTopic.HelpTopicStatus.NOT_RESOLVED;

            if (currentTopicTitle == null || currentTopicUid == null) {
                showTopicError();
                return;
            }

            topicTitleTextView.setText(currentTopicTitle);
            topicDescriptionTextView.setText(description);
            topicLikesTextView.setText(String.valueOf(likes));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            showTopicError();
        }
    };

    ValueEventListener helpTopicImagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot imageURLsnapshot : snapshot.getChildren()) {
                String imageURL = (String)imageURLsnapshot.getValue();
                topicImagesList.add(new ViewHelpTopicImageItem(imageURL));
                topicImagesAdapter.notifyDataSetChanged();
            }

            if (topicImagesList.size() == 0) {
                imagesLabelTextView.setText(R.string.view_help_topic_no_attached_images_label);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // TODO: Display error if post can't be read
        }
    };

    ValueEventListener helpTopicSolutionsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            topicSolutionsList.clear();
            for (DataSnapshot solutionSnapshot : snapshot.getChildren()) {
                String uid = solutionSnapshot.getKey();
                String body = (String) solutionSnapshot.child("body").getValue();
                Long likes = (Long) solutionSnapshot.child("likes").getValue();
                String authorUid = (String) solutionSnapshot.child("authorUid").getValue();
                String authorName = (String) solutionSnapshot.child("authorName").getValue();

                TopicSolutionItem solutionItem = new
                        TopicSolutionItem(uid, body, authorUid, authorName, likes);
                topicSolutionsList.add(solutionItem);
            }
            topicSolutionsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // TODO: Display error if post can't be read
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_help_topic);

        Bundle extras = getIntent().getExtras();
        helpTopicUid = extras.getString("HELP_TOPIC_UID");

        if (helpTopicUid == null) {
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        topicTitleTextView = findViewById(R.id.view_topic_activity_title);
        topicDescriptionTextView = findViewById(R.id.view_topic_activity_description);
        topicLikesTextView = findViewById(R.id.view_topic_activity_likes);
        imagesLabelTextView = findViewById(R.id.view_help_topic_attached_images_label);
        solutionEditText = findViewById(R.id.view_help_topic_activity_add_solution_text_input);
        postSolutionButton = findViewById(R.id.view_help_topic_activity_add_solution_button);

        postSolutionButton.setOnClickListener(v -> onPostSolutionButtonClicked());

        topicImagesList = new ArrayList<>();

        topicImagesRecyclerView = findViewById(R.id.view_topic_activity_topic_images_recycler_view);
        topicImagesRecyclerView.setHasFixedSize(true);
        topicImagesLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        topicImagesAdapter = new ViewHelpTopicImagesAdapter(topicImagesList);
        topicImagesRecyclerView.setLayoutManager(topicImagesLayoutManager);
        topicImagesRecyclerView.setAdapter(topicImagesAdapter);

        topicSolutionsList = new ArrayList<>();

        topicSolutionsRecyclerView = findViewById(R.id.view_help_topic_activity_solutions_recycler_view);
        topicSolutionsRecyclerView.setHasFixedSize(true);
        topicSolutionsLayoutManager = new LinearLayoutManager(this);
        topicSolutionsAdapter = new HelpTopicSolutionsAdapter(topicSolutionsList);
        topicSolutionsRecyclerView.setLayoutManager(topicSolutionsLayoutManager);
        topicSolutionsRecyclerView.setAdapter(topicSolutionsAdapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabaseRef
                .child("help-topics")
                .child(helpTopicUid)
                .addValueEventListener(helpTopicListener);

        mDatabaseRef
                .child("help-topics")
                .child(helpTopicUid)
                .child("imageURLs")
                .addValueEventListener(helpTopicImagesListener);

        Query solutionsQuery = mDatabaseRef
                .child("help-topics")
                .child(helpTopicUid)
                .child("solutions")
                .orderByChild("dateTime");

        solutionsQuery.addValueEventListener(helpTopicSolutionsListener);
    }

    private void showTopicError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.view_help_topic_activity_topic_not_found_error)
                .setCancelable(false)
                .setPositiveButton(R.string.view_help_topic_activity_topic_not_found_error_ok_button, (dialog, id) -> {});
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void onPostSolutionButtonClicked() {
        String uid = UUID.randomUUID().toString();
        String body = solutionEditText.getText().toString();
        String authorUid = mAuth.getCurrentUser().getUid();
        String authorDisplayName = mAuth.getCurrentUser().getDisplayName();

        HelpTopicSolution helpTopicSolution = new HelpTopicSolution();
        helpTopicSolution.body = body;
        helpTopicSolution.authorUid = authorUid;
        helpTopicSolution.authorName = authorDisplayName;
        helpTopicSolution.likes = (long)0;
        helpTopicSolution.dateTime = System.currentTimeMillis() * -1;

        mDatabaseRef
                .child("help-topics")
                .child(helpTopicUid)
                .child("solutions")
                .child(uid)
                .setValue(helpTopicSolution);

        String logUid = UUID.randomUUID().toString();
        UserActivityLog activityLog = new UserActivityLog();
        activityLog.activityType = UserActivityType.ADDED_SOLUTION;
        activityLog.topicTitle = currentTopicTitle;
        activityLog.topicUid = currentTopicUid;
        activityLog.userUid = mAuth.getCurrentUser().getUid();
        activityLog.dateTime = helpTopicSolution.dateTime;

        mDatabaseRef.child("users-activity-logs")
                .child(mAuth.getCurrentUser().getUid())
                .child(logUid)
                .setValue(activityLog);
    }
}