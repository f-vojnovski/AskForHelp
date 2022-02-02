package com.example.askforhelp.firebase.model;

import com.google.firebase.database.Exclude;

public class HelpTopicSolution {
    public HelpTopicSolution() {

    }

    @Exclude
    public String uid;
    public String body;
    public String authorUid;
    public String authorName;
    public Long likes;
    public Long dateTime;
}
