package com.thinkhodl.bumblebee;


public class Utils {

    public static String WORD_DATABASE = "words";
    public static String WORD_LEVEL = "level";
    public static String WORD_WORD = "word";

    public static String GAME_DATABASE = "games";
    public static String GAME_USER_ID = "userID";
    public static String GAME_TIMESTAMP = "timestamp";
    public static String GAME_TOTAL_SCORE = "totalScore";
    public static String GAME_PLAYED_WORDS = "playedWords";

    public static String HIGHSCORE_DATABASE = "highscore";
    public static String HIGHSCORE_SCORE = "score";
    public static String HIGHSCORE_USER = "user";
    public static String HIGHSCORE_TIMESTAMP = "timestamp";


    public static String LEVEL_DATABASE = "levels";
    public static String LEVEL_COEFFICIENT = "coefficient";
    public static String LEVEL_LEVEL = "level";
    public static String LEVEL_DESCRIPTION = "description";
    public static String LEVEL_TITLE = "title";


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
