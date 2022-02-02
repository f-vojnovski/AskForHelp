package com.example.askforhelp.adapters;

import com.example.askforhelp.firebase.model.HelpTopic;

public class HelpTopicItem {
    private String uid;
    private String title;
    private HelpTopic.HelpTopicStatus status;
    private Long likes;
    private String authorName;
    private String authorUid;

    public HelpTopicItem(String uid, String title, HelpTopic.HelpTopicStatus status, Long likes,
                         String authorUid, String authorName) {
        this.uid = uid;
        this.title = title;
        this.status = status;
        this.likes = likes;
        this.authorUid = authorUid;
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public HelpTopic.HelpTopicStatus getStatus() {
        return status;
    }

    public Long getLikes() {
        return likes;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getAuthorName() {
        return authorName;
    }
}
