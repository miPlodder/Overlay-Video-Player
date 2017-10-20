package com.example.saksham.overlayscreenshort.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by saksham on 10/9/2017.
 */

public class PlaylistPOJO  {

    String name;
    Bitmap thumbnail;
    Uri uri;

    public PlaylistPOJO(String name, Bitmap thumbnail, Uri uri) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.uri = uri;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
