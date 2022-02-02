package com.example.askforhelp.adapters;

import com.example.askforhelp.datainterface.UserActivityType;

public class UserActivityLogItem {
    private String topicUid;
    private String topicTitle;
    private UserActivityType activityType;

    public UserActivityLogItem(String topicUid, String topicTitle, UserActivityType activityType) {
        this.topicUid = topicUid;
        this.topicTitle = topicTitle;
        this.activityType = activityType;
    }

    public String getTopicUid() {
        return topicUid;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public UserActivityType getActivityType() {
        return activityType;
    }
}
