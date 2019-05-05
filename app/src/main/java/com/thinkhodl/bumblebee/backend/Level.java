package com.thinkhodl.bumblebee.backend;

public class Level {

    private String mTitle;
    private int mLevel;
    private float mCoefficient = 1;
    private String mDescription = null;

    public Level(String mTitle, int mLevel, float mCoefficient, String mDescription) {
        this.mTitle = mTitle;
        this.mLevel = mLevel;
        this.mCoefficient = mCoefficient;
        this.mDescription = mDescription;
    }

    public Level(String mTitle, int mLevel, float mCoefficient) {
        this.mTitle = mTitle;
        this.mLevel = mLevel;
        this.mCoefficient = mCoefficient;
    }

    public Level() {
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public float getCoefficient() {
        return mCoefficient;
    }

    public void setCoefficient(float mCoefficient) {
        this.mCoefficient = mCoefficient;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mLevelTitle) {
        this.mTitle = mLevelTitle;
    }
}
