package com.lovoctech.yakshanaada.model;

import androidx.annotation.RawRes;

public class Tanpur {

    @RawRes
    private final int pa;
    @RawRes
    private final int ma;
    private final String mediaId;
    private final String title;
    private final String description;

    public Tanpur(int pa, int ma, String mediaId, String title, String description) {
        this.pa = pa;
        this.ma = ma;
        this.mediaId = mediaId;
        this.title = title;
        this.description = description;
    }

    public int getPa() {
        return pa;
    }

    public int getMa() {
        return ma;
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
}
