package com.bumblebeem.android.bumblebeem;

/**
 * Created by n3v10t on 07/10/16.
 */

public class Level {
    private String level;
    private int levelInt;
    private int imageRes;

    public Level(String level, int levelInt, int imageRes)
    {
        this.level = level;
        this.levelInt = levelInt;
        this.imageRes =imageRes;
    }

    public int getImageRes() {
        return imageRes;
    }

    public int getLevelInt() {
        return levelInt;
    }

    public String getLevel() {
        return level;
    }

}
