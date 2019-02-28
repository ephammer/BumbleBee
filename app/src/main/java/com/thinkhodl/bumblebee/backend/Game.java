package com.thinkhodl.bumblebee.backend;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Game {

    private Timestamp mTimestamp;
    private ArrayList<PlayedWord> mPlayedWords;
    private String mUserID;
    private int mTotalScore;

    public Game() {}

    public Game(String mUserID, Timestamp mTimestamp, ArrayList<PlayedWord> mPlayedWords) {
        this.mUserID = mUserID;
        this.mTimestamp = mTimestamp;
        this.mPlayedWords = mPlayedWords;

    }

    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setDate(Timestamp mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public ArrayList<PlayedWord> getPlayedWords() {
        return mPlayedWords;
    }

    public void setPlayedWords(ArrayList<PlayedWord> mPlayedWords) {
        this.mPlayedWords = mPlayedWords;
        mTotalScore = 0;
        for (int i = 0; i < mPlayedWords.size(); i++) {
            mTotalScore += mPlayedWords.get(i).getScore();
        }
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public int getTotalScore() {
        return mTotalScore;
    }

    public void setTotalScore(int mTotalScore) {
        this.mTotalScore = mTotalScore;
    }
}
