package com.example.askforhelp.adapters;

public class TopicSolutionItem {
    private String uid;
    private String body;
    private String authorUid;
    private String authorName;
    private Long likes;

    public TopicSolutionItem(String uid, String body, String authorUid, String authorName, Long likes) {
        this.uid = uid;
        this.body = body;
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.likes = likes;
    }

    public String getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Long getLikes() {
        return likes;
    }
}
