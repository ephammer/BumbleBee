package com.thinkhodl.bumblebee.backend;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class SpeedGame extends Game {

    public SpeedGame(String mUserID, Timestamp mTimestamp, ArrayList<PlayedWord> mPlayedWords) {
        super(mUserID, mTimestamp, mPlayedWords);
    }
}
