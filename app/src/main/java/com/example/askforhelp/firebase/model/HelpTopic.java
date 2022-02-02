package com.example.askforhelp.firebase.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HelpTopic {
    public enum HelpTopicStatus {
        NOT_RESOLVED,
        RESOLVED
    }

    public HelpTopic() {
        imageURLs = new ArrayList<>();
    }

    @Exclude
    public String uid;
    public String authorUid;
    public String authorName;
    public String title;
    public String description;
    public HelpTopicStatus status;
    public long likes;
    public List<String> imageURLs;
    public Long dateTime;
}
