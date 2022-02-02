package com.example.askforhelp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.askforhelp.adapters.AddNewTopicImageItem;
import com.example.askforhelp.adapters.AddNewTopicImagesAdapter;
import com.example.askforhelp.datainterface.UserActivityType;
import com.example.askforhelp.firebase.model.HelpTopic;
import com.example.askforhelp.firebase.model.UserActivityLog;
import com.example.askforhelp.util.BitmapToUriConverter;
import com.example.askforhelp.util.FileExtensionFinder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: Add listeners to inform user when post is complete (do this later)
// TODO: Change logic in this section so we don't make a copy of the image in storage
public class NewHelpTopicFragment extends Fragment {
    private EditText topicTitle;
    private EditText topicDescription;
    private Button addPhotoFromGalleryButton;
    private Button takePhotoFromCameraButton;
    private Button postButton;
    private ProgressBar progressBar;

    private RecyclerView attachedImagesRecyclerView;
    private AddNewTopicImagesAdapter attachedImagesAdapter;
    private RecyclerView.LayoutManager attachedImagesLayoutManager;
    private ArrayList<AddNewTopicImageItem> attachedImagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    String algoliaAppId = "II2ZUMTUMY";
    String algoliaApiKey = "5e71d55a3a8c6c2d29255090a1e02b97";
    String algoliaFriendSerachIndex = "help-topics";

    Client algoliaClient;
    Index algoliaIndex;

    public NewHelpTopicFragment() {
        // Required empty public constructor
    }

    public static NewHelpTopicFragment newInstance() {
        NewHelpTopicFragment fragment = new NewHelpTopicFragment();
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
        return inflater.inflate(R.layout.fragment_new_help_topic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        topicTitle = view.findViewById(R.id.new_topic_fragment_title);
        topicDescription = view.findViewById(R.id.new_topic_fragment_description);
        addPhotoFromGalleryButton = view.findViewById(R.id.new_topic_fragment_add_photo_button);
        takePhotoFromCameraButton = view.findViewById(R.id.new_topic_fragment_take_photo_button);
        postButton = view.findViewById(R.id.new_topic_fragment_post_button);
        progressBar = view.findViewById(R.id.new_topic_fragment_progress_bar);

        addPhotoFromGalleryButton.setOnClickListener(v -> onAddPhotoFromGalleryButtonClicked());
        takePhotoFromCameraButton.setOnClickListener(v -> onTakePhotoFromCameraButtonClicked());
        postButton.setOnClickListener(v -> onPostButtonClicked());

        attachedImagesList = new ArrayList<>();

        attachedImagesRecyclerView = view.findViewById(R.id.new_topic_fragment_images_recycler_view);
        attachedImagesRecyclerView.setHasFixedSize(true);
        attachedImagesLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        attachedImagesAdapter = new AddNewTopicImagesAdapter(attachedImagesList);
        attachedImagesRecyclerView.setLayoutManager(attachedImagesLayoutManager);
        attachedImagesRecyclerView.setAdapter(attachedImagesAdapter);

        algoliaClient = new Client(algoliaAppId, algoliaApiKey);
        algoliaIndex = algoliaClient.getIndex(algoliaFriendSerachIndex);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    onPhotoAttached(imageBitmap);
                }
            });

    ActivityResultLauncher<Intent> attachImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    try {
                        Uri imageUri = data.getData();
                        InputStream imageStream = getActivity()
                                .getContentResolver()
                                .openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        onPhotoAttached(selectedImage);
                    } catch (FileNotFoundException e) {
                        // TODO: Inform user that file is not found
                    }
                }
            });

    private void onAddPhotoFromGalleryButtonClicked() {
        Intent attachImageIntent = new Intent(Intent.ACTION_PICK);
        attachImageIntent.setType("image/**");
        attachImageResultLauncher.launch(attachImageIntent);
    }

    private void onTakePhotoFromCameraButtonClicked() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityResultLauncher.launch(takePictureIntent);
    }

    private void onPostButtonClicked() {
        HelpTopic helpTopic = new HelpTopic();
        helpTopic.uid = UUID.randomUUID().toString();
        helpTopic.title = topicTitle.getText().toString();
        helpTopic.description = topicDescription.getText().toString();
        helpTopic.authorUid = mAuth.getCurrentUser().getUid();
        helpTopic.authorName = mAuth.getCurrentUser().getDisplayName();
        helpTopic.status = HelpTopic.HelpTopicStatus.NOT_RESOLVED;
        helpTopic.likes = 0;
        helpTopic.dateTime = System.currentTimeMillis() * -1;
        progressBar.setVisibility(View.VISIBLE);
        postButton.setEnabled(false);
        takePhotoFromCameraButton.setEnabled(false);
        addPhotoFromGalleryButton.setEnabled(false);

        if (attachedImagesList.size() == 0) {
            onAllPhotosUploaded(helpTopic);
        }

        AtomicInteger completedUploads = new AtomicInteger();

        for (int i = 0; i<attachedImagesList.size(); ++i) {
            Bitmap image = attachedImagesList.get(i).getmImageResource();
            Uri uri = BitmapToUriConverter.convertBitmapToUri(getActivity(), image);
            StorageReference ref = mStorageRef.child("images/"
                    + helpTopic.uid + "/"
                    + i + "." + FileExtensionFinder.findExtensionForFile(getActivity(), uri));

            UploadTask uploadTask = ref.putFile(uri);
            uploadTask.continueWithTask(task -> ref.getDownloadUrl()).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        helpTopic.imageURLs.add(downloadUri.toString());
                        completedUploads.getAndIncrement();
                        if (completedUploads.get() == attachedImagesList.size()) {
                            onAllPhotosUploaded(helpTopic);
                        }
                    } else {
                        showDatabaseError();
                    }
                }
            });
        }
    }

    private void onPhotoAttached(Bitmap image) {
        attachedImagesList.add(new AddNewTopicImageItem(image));
        attachedImagesAdapter.notifyDataSetChanged();
    }

    private void onAllPhotosUploaded(HelpTopic helpTopic) {
        mDatabaseRef.child("help-topics")
                .child(helpTopic.uid)
                .setValue(helpTopic)
                .addOnSuccessListener(aVoid -> {
                    pushTopicToAlgoliaIndex(helpTopic);
                    openNewHelpTopic(helpTopic.uid);
                })
                .addOnFailureListener(e -> showDatabaseError());

        String logUid = UUID.randomUUID().toString();
        UserActivityLog activityLog = new UserActivityLog();
        activityLog.activityType = UserActivityType.ADDED_TOPIC;
        activityLog.topicTitle = helpTopic.title;
        activityLog.topicUid = helpTopic.uid;
        activityLog.userUid = mAuth.getCurrentUser().getUid();
        activityLog.dateTime = helpTopic.dateTime;

        mDatabaseRef.child("users-activity-logs")
                .child(mAuth.getCurrentUser().getUid())
                .child(logUid)
                .setValue(activityLog);
    }

    private void pushTopicToAlgoliaIndex(HelpTopic helpTopic) {
        try {
            JSONObject object = new JSONObject()
                    .put("uid", helpTopic.uid)
                    .put("title", helpTopic.title)
                    .put("likes", 0)
                    .put("authorUid", helpTopic.authorUid)
                    .put("authorName", helpTopic.authorName);

            algoliaIndex.addObjectAsync(object, helpTopic.uid, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openNewHelpTopic(String topicUid) {
        Intent intent = new Intent(getActivity(), ViewHelpTopicActivity.class);
        intent.putExtra("HELP_TOPIC_UID", topicUid);
        startActivity(intent);
        HomeActivity parentActivity = (HomeActivity)this.getActivity();
        parentActivity.setShouldRefreshOnResume(true);
    }

    private void showDatabaseError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.new_topic_fragment_database_error)
                .setCancelable(false)
                .setPositiveButton(R.string.new_topic_fragment_database_error_ok_button, (dialog, id) -> {
                    progressBar.setVisibility(View.GONE);
                    postButton.setEnabled(true);
                    takePhotoFromCameraButton.setEnabled(true);
                    addPhotoFromGalleryButton.setEnabled(true);
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}