package com.bumblebeem.android.bumblebeem;

/**
 * Created by n3v10t on 18/09/16.
 */
public class Word
{
    private String word; // String containing the actual word
    private String playerInput; // String containing the players input word
    private boolean equal = false;
    private int level = 1; // level of difficulty of the current word

    public Word(String word, String playerInput , int level)
    {
        this.word = word;
        this.playerInput = playerInput;
        this.level = level;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public void setPlayerInput(String playerInput)
    {
        this.playerInput = playerInput;
        this.setEqual();
    }

    public String getPlayerInput()
    {
        return playerInput;
    }

    public String getWord()
    {
        return word;
    }

    // Computes if two words are equal
    private static int minimum(int a, int b, int c)
    {
        return Math.min(Math.min(a, b), c);
    }

    // If two words are equal returns 0
    private int computeLevenshteinDistance()
    {
        if(playerInput!=null)
        {
            int[][] distance = new int[word.length() + 1][playerInput.length() + 1];

            for (int i = 0; i <= word.length(); i++)
                distance[i][0] = i;
            for (int j = 1; j <= playerInput.length(); j++)
                distance[0][j] = j;

            for (int i = 1; i <= word.length(); i++)
                for (int j = 1; j <= playerInput.length(); j++)
                    distance[i][j] = minimum(
                            distance[i - 1][j] + 1,
                            distance[i][j - 1] + 1,
                            distance[i - 1][j - 1] + ((word.charAt(i - 1) == playerInput.charAt(j - 1)) ? 0 : 1));

            return distance[word.length()][playerInput.length()];
        }
        return -1;
    }

    // Function that checks if user input is equal too word of list
    public void setEqual()
    {
        if(computeLevenshteinDistance()==0)
            equal = true;
    }

    public int score()
    {
        int tmp = ( word.length() - computeLevenshteinDistance() ) / word.length()  * 10 * level;

        if(tmp<0)
            return 0;
        else
        {
            return tmp;
        }

    }

    public boolean isEqual() {
        return equal;
    }
}