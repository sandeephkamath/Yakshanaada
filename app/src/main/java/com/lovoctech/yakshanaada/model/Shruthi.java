package com.lovoctech.yakshanaada.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RawRes;

public class Shruthi implements Parcelable {

    public static final String SHRUTHI = "shruthi";
    @RawRes
    private final int resource;
    private final String mediaId;
    private final String title;
    private final String description;
    private final int bitmapResource;


    public Shruthi(@RawRes int resource, String mediaId, String title, String description, int bitmapResource) {
        this.resource = resource;
        this.mediaId = mediaId;
        this.title = title;
        this.description = description;
        this.bitmapResource = bitmapResource;
    }

    protected Shruthi(Parcel in) {
        resource = in.readInt();
        mediaId = in.readString();
        title = in.readString();
        description = in.readString();
        bitmapResource = in.readInt();
    }

    public static final Creator<Shruthi> CREATOR = new Creator<Shruthi>() {
        @Override
        public Shruthi createFromParcel(Parcel in) {
            return new Shruthi(in);
        }

        @Override
        public Shruthi[] newArray(int size) {
            return new Shruthi[size];
        }
    };

    @RawRes
    public int getUri() {
        return resource;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getBitmapResource() {
        return bitmapResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(resource);
        parcel.writeString(mediaId);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(bitmapResource);
    }
}
