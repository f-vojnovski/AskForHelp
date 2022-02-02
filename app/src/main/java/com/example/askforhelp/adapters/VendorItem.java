package com.example.askforhelp.adapters;

public class VendorItem {
    private String name;
    private String description;
    private String location;

    public VendorItem(String name, String biography, String location) {
        this.name = name;
        this.description = biography;
        this.location = location;
    }

    public String getVendorName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() { return location; }
}
