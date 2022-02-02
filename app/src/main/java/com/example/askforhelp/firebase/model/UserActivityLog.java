package com.example.askforhelp.firebase.model;

import com.example.askforhelp.datainterface.UserActivityType;
import com.google.firebase.database.Exclude;

public class UserActivityLog {
    public UserActivityLog() {}

    @Exclude
    public String userUid;
    public UserActivityType activityType;
    public String topicUid;
    public String topicTitle;
    public Long dateTime;
}
