package com.thinkhodl.bumblebee.backend;


import com.google.firebase.Timestamp;

public class HighScore {

    private String id;
    private int mScore;
    private Timestamp mTimeStamp;
    private String mUser;

    public HighScore() {
    }

    public HighScore(String id, int mScore, Timestamp mTimeStamp, String mUser) {
        this.id = id;
        this.mScore = mScore;
        this.mTimeStamp = mTimeStamp;
        this.mUser = mUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int mScore) {
        this.mScore = mScore;
    }

    public Timestamp getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(Timestamp mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        this.mUser = user;
    }
}
