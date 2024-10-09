package com.example.musicapp;

public class PlaylistsMadeForYou {
    private String mName;
    private int mImage;
    public PlaylistsMadeForYou(String name, int image) {
        mName = name;
        mImage = image;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmImage() {
        return mImage;
    }

    public void setmImage(int mImage) {
        this.mImage = mImage;
    }
}
