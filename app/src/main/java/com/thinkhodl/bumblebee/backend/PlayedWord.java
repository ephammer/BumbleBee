package com.thinkhodl.bumblebee.backend;

import com.thinkhodl.bumblebee.Utils;

public class PlayedWord extends Word {

    private String mUserInput;
    private Boolean mResult = false;
    private int mScore;

    public PlayedWord() {
    }

    public PlayedWord(String mWordID, int mLevel, String mWord) {
        super(mWordID, mLevel, mWord);
    }

    public PlayedWord(String mWordID, int mLevel, String mWord, String mUserInput) {
        super(mWordID, mLevel, mWord);
        this.mUserInput = mUserInput;
    }

    public PlayedWord(Word mWord, String mUserInput)
    {
        super(mWord.getWordID(), mWord.getLevel(), mWord.getWord());
        this.mUserInput = mUserInput;
        this.mResult = (mWord.getWord().equals(mUserInput));
        mScore = Utils.score(mWord.getWord(),mUserInput,getLevel());
    }

    public String getUserInput() {
        return mUserInput;
    }

    public void setUserInput(String mUserInput) {
        this.mUserInput = mUserInput;
    }

    public Boolean getResult() {
        return mResult;
    }

    public void setResult(Boolean mResult) {
        this.mResult = mResult;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int mScore) {
        this.mScore = mScore;
    }
}
