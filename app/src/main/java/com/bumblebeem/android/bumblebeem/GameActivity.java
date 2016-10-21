package com.bumblebeem.android.bumblebeem;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    // Initialize the index of the current Word that is played in the list of Words
    private int indexOfActualWord = 0;

    // ArrayList of Words that contains the words of the actual game
    public static ArrayList<Word> words;

    // Initialize TextToSpeech
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /* First we get the parameters for the configuration for the actual game
         * The parameters are send over by the last Activity as Intent Extras.
         * The actual Level File is contained in the levelString field
         * The level number is contained in the level field*/
        Intent levelIntent = getIntent();
        final String levelFile = levelIntent.getStringExtra("levelString");
        final int levelInt = levelIntent.getIntExtra("level", 1);

        // Get all the words contained in the level file
        Read read = new Read(GameActivity.this, levelFile, levelInt);

        // Load the words in an ArrayList of Words
        words = read.loadFile();

        // Narrow the number of Words down to 30 and randomize the list
        words = pickNRandom(words, 30);

        // Load TTS in files
        LoadTTS loadTTS = new LoadTTS(getApplicationContext(), words);

        // Initialize the different UI Elements
        final ImageButton replayButton = (ImageButton) findViewById(R.id.replay_button);
        final ImageButton skipButton = (ImageButton) findViewById(R.id.skip_button);
        final CustomEditText playerInput = (CustomEditText) findViewById(R.id.editText);
        final TextView countDownTextView = (TextView) findViewById(R.id.countdown_textView);
        final GradientDrawable countDowTextViewColor = (GradientDrawable) countDownTextView.getBackground();
        // Initialize the input method
        final InputMethodManager keyboard = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

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

        // Force All caps in the EditText field
        playerInput.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        /* Set OnEditorActionListener on the EditText field
         * The listener listens if the user enter the ENTER key
         * When activated the content of the EditText field is saved in the corresponding
         * playerInput field in the actual word */
        playerInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Get input of player of EditText field
                    words.get(indexOfActualWord).setPlayerInput(
                            playerInput.getText().toString().trim().toLowerCase());

                    if (indexOfActualWord < words.size() - 1) {
                        // Set entry filed blank
                        playerInput.setText("");

                        // Force keyboard to stay open
                        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);

                        // Play next word in list
                        indexOfActualWord++;
                        playWord(words.get(indexOfActualWord).getWord());
                    }
                    return true;
                } else
                    return false;
            }
        });

        // Start with the first word of the list
        playWord(words.get(indexOfActualWord).getWord());

        // Start CountDownTimer
        new CountDownTimer(20000, 1000) {


            @Override
            public void onTick(long l) {
                countDownTextView.setText(String.valueOf(l / 1000));

                if (l < 6000) {
                    countDowTextViewColor.setColor(ContextCompat.getColor(GameActivity.this, R.color.circle2));

                }
            }

            @Override
            public void onFinish() {
                // Hide keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                // Hide all the views exept the counter view
                playerInput.setVisibility(View.GONE);
                replayButton.setVisibility(View.GONE);
                skipButton.setVisibility(View.GONE);

                // Set countdown TextView to done
                countDowTextViewColor.setColor(null);
                countDownTextView.setText(R.string.done_string);

                // Trim the list to the actual numbers of entries of the player
                words = new ArrayList<>(words.subList(0, indexOfActualWord));

                //  Start the ResultActivity when the counter is finished  *//*
                Intent i = new Intent(GameActivity.this, ResultsActivity.class);
                i.putExtra("level", levelInt);
                i.putExtra("levelString", levelFile );
                startActivity(i);

                /* Finish current Activity so as that the player can't come back on this activity
                 * when hitting the back button
                 */
                finish();
            }
        }.start();

    }

    // Function that plays the actual word
    private void playWord(final String word) {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);

                    textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

    }

    /* Function that takes as input a ArrayList of Words
     * shuffles the list and randomizes the list */
    public static ArrayList<Word> pickNRandom(ArrayList<Word> lst, int n) {
        ArrayList<Word> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, n));
    }

    @Override
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
