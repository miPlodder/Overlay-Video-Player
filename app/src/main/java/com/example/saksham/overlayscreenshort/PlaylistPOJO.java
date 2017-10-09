package com.example.saksham.overlayscreenshort;

import android.graphics.Bitmap;

/**
 * Created by saksham on 10/9/2017.
 */

public class PlaylistPOJO {

    String name;
    Bitmap thumbnail;

    public PlaylistPOJO(String name, Bitmap thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
