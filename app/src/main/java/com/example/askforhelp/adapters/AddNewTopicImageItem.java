package com.example.askforhelp.adapters;

import android.graphics.Bitmap;

public class AddNewTopicImageItem {
    private Bitmap mImageResource;

    public AddNewTopicImageItem(Bitmap mImageResource) {
        this.mImageResource = mImageResource;
    }

    public Bitmap getmImageResource() {
        return mImageResource;
    }
}
