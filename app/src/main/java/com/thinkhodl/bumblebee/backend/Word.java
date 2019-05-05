package com.thinkhodl.bumblebee.backend;

public class Word {

    private int mLevel;
    private String mWord;
    private String mLanguage = "english";
    private String mWordID;

    public Word() {
    }

    public Word(String mWordID, int mLevel, String mWord) {
        this.mWordID = mWordID;
        this.mLevel = mLevel;
        this.mWord = mWord;
    }

    public String getWordID() {
        return mWordID;
    }

    public void setWordID(String mWordID) {
        this.mWordID = mWordID;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String mWord) {
        this.mWord = mWord;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }
}
