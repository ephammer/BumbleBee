package com.bumblebeem.android.bumblebeem;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    // Initialize the index of the current Word that is played in the list of Words
    int indexOfActualWord = 0;

    // ArrayList of Words that contains the words of the actual game
    ArrayList<Word> words;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /* First we get the parameters for the configuration for the actual game
         * The parameters are send over by the last Activity as Intent Extras.
         * The actual Level File is contained in the levelString field
         * The level number is contained in the level field*/
        Intent levelIntent = getIntent();
        String levelFile = levelIntent.getStringExtra("levelString");
        int levelInt = levelIntent.getIntExtra("level", 1);

        // Get all the words contained in the level file
        Read read = new Read(GameActivity.this, levelFile, levelInt);

        // Load the words in an ArrayList of Words
        words = read.loadFile();

        // Narrow the number of Words down to 30 and randomize the list
        words = pickNRandom(words,30);

        // Initialize the different UI Elements
        ImageButton replayButton = (ImageButton) findViewById(R.id.replay_button);
        ImageButton skipButton = (ImageButton) findViewById(R.id.skip_button);
        final CustomEditText playerInput = (CustomEditText) findViewById(R.id.editText);

        /* Set an OnClickListener on the replay Button
         * When the player clicks on the replay button the actual word in the list is replayed */
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWord(words.get(indexOfActualWord).getWord());
            }
        });

        /* Set OnClickListener on the skip button
         * When the skip button is clicked the next playWord function is called
         * on the next word in the List.
         * If the word is the last word in the list the actual word is replayed */
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks if the current word is last of list
                if (indexOfActualWord < (words.size() - 1)) {
                    /* if word is not last of list increment indexOfWords by one
                     * so as to go to next word in array
                    */
                    indexOfActualWord++;
                    // set EditText field blank
                    playerInput.setText("");
                    // playWord on next word in List
                    playWord(words.get(indexOfActualWord).getWord());

                } else // if we are in the end of list play current word again
                    playWord(words.get(indexOfActualWord).getWord());
            }
        });



    }

    // Function that plays the actual word
    private void playWord(String word) {
    }

    /* Function that takes as input a ArrayList of Words
     * shuffles the list and randomizes the list */
    public static ArrayList<Word> pickNRandom(ArrayList<Word> lst, int n) {
        ArrayList<Word> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, n));
    }
}
