package com.example.askforhelp.adapters;

public class HandymanItem {
    private String uid;
    private String name;
    private String biograhy;
    private float distance;

    public HandymanItem(String uid, String name, String biograhy, float distance) {
        this.name = name;
        this.biograhy = biograhy;
        this.distance = distance;
    }

    public String getUid() {
        return uid;
    }

    public String getHandymanName() {
        return name;
    }

    public String getBiograhy() {
        return biograhy;
    }

    public float getDistance() {
        return distance;
    }
}
