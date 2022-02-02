package com.example.askforhelp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.askforhelp.adapters.HelpTopicsAdapter;
import com.example.askforhelp.adapters.HelpTopicItem;
import com.example.askforhelp.firebase.model.HelpTopic;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchHelpTopicsFragment extends Fragment {
    private String algoliaAppId = "II2ZUMTUMY";
    private String algoliaApiKey = "24c6a29c19c5ed926c4d0b74507554f4";
    String algoliaFriendSearchIndex = "help-topics";

    private Client algoliaClient;
    private Index algoliaIndex;

    private TextInputEditText searchQueryTextInputEditText;
    private Button searchButton;

    private RecyclerView topicsRecyclerView;
    private HelpTopicsAdapter topicsAdapter;
    private RecyclerView.LayoutManager topicsLayoutManager;
    private ArrayList<HelpTopicItem> topicsList;

    public SearchHelpTopicsFragment() {
        // Required empty public constructor
    }

    public static SearchHelpTopicsFragment newInstance() {
        SearchHelpTopicsFragment fragment = new SearchHelpTopicsFragment();
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
        return inflater.inflate(R.layout.fragment_search_help_topics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        algoliaClient = new Client(algoliaAppId, algoliaApiKey);
        algoliaIndex = algoliaClient.getIndex(algoliaFriendSearchIndex);

        searchQueryTextInputEditText = view.findViewById(R.id.search_help_topics_fragment_search_text_input);
        searchButton = view.findViewById(R.id.search_help_topics_fragment_search_button);

        topicsList = new ArrayList<>();

        topicsRecyclerView = view.findViewById(R.id.search_help_topics_fragment_results_recycler_view);
        topicsRecyclerView.setHasFixedSize(true);
        topicsLayoutManager = new LinearLayoutManager(getActivity());
        topicsAdapter = new HelpTopicsAdapter(topicsList);
        topicsRecyclerView.setLayoutManager(topicsLayoutManager);
        topicsRecyclerView.setAdapter(topicsAdapter);

        topicsAdapter.setOnHelpTopicClickedListener(position -> {
            onTopicSelected(topicsList.get(position).getUid());
        });

        searchButton.setOnClickListener(v -> onSearchButtonClicked());
    }

    private void onSearchButtonClicked() {
        String query = searchQueryTextInputEditText.getText().toString();
        algoliaIndex.searchAsync(new Query(query), completionHandler);
    }

    private CompletionHandler completionHandler = (result, e) -> {
        try {
            topicsList.clear();
            JSONArray hits = result.getJSONArray("hits");
            if (hits.length() == 0) {
                onNoResultsFound();
            }

            for (int i = 0; i<hits.length(); ++i) {
                JSONObject foundTopic = hits.getJSONObject(i);
                String uid = foundTopic.getString("uid");
                String title = foundTopic.getString("title");
                Long likes = foundTopic.getLong("likes");
                String authorName = foundTopic.getString("authorName");
                String authorUid = foundTopic.getString("authorUid");

                HelpTopicItem helpTopic =
                        new HelpTopicItem(uid,
                                title,
                                HelpTopic.HelpTopicStatus.NOT_RESOLVED,
                                likes,
                                authorUid,
                                authorName);
                topicsList.add(helpTopic);
            }
            topicsAdapter.notifyDataSetChanged();
        } catch (JSONException jsonException) {
            // TODO: Inform user that there was error fetching results
        }
    };

    private void onTopicSelected(String topicUid) {
        Intent intent = new Intent(getActivity(), ViewHelpTopicActivity.class);
        intent.putExtra("HELP_TOPIC_UID", topicUid);
        startActivity(intent);
    }

    private void onNoResultsFound() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.search_help_topics_fragment_no_results_found)
                .setCancelable(false)
                .setPositiveButton(R.string.search_help_topics_fragment_no_results_alert_ok_button, (dialog, id) -> {});
        AlertDialog alert = builder.create();
        alert.show();
    }
}