package com.thinkhodl.bumblebee;

import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.backend.PlayedWord;

import java.util.Map;

public class Utils {

    public static String WORD_LEVEL = "level";
    public static String WORD_WORD = "word";

    // Computes if two words are equal
    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    // If two words are equal returns 0
    private static int computeLevenshteinDistance(String playerInput, String word){
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

    public static int score(String playerInput, String word, int level){
        int tmp = ( word.length() - computeLevenshteinDistance(playerInput,word) ) / word.length()  * 10 * level;

        if(tmp<0)
            return 0;
        else
        {
            return tmp;
        }

    }


}
