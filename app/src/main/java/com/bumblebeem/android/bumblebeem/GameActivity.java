package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextToSpeech tts;

    static ArrayList<Word> words = new ArrayList<>();    // Array of words for game
    static ArrayList<Word> randomWordsList = new ArrayList<>();    // Copy of array with shuffled words
    private int indexOfActualWord = 0;     // Current index in array of words that the player is playing


    private String levelFile;
    private int levelInt;
    private CountDownTimer countDownTimer;

    /**
     * Initialize different UI elements
     */
    private TextView countDownTextView;
    private GradientDrawable countDowTextViewColor;
    private CustomEditText input;    // Initialize editText
    private ImageButton replayButton;        // Initialize replay button
    private ImageButton skipButton;    // Initialize forward button

    /**
     * Internal storage file path
     */
    public String SD_DATA_PATH;

    /**
     * Initialize the keyboard
     */
    private InputMethodManager imm;

    /**
     * Internal Storage Directory where the sound files are saved
     */
    private File audioFileDirectory;

    /**
     * Handles playback of all the sound files
     */
    private MediaPlayer mMediaPlayer;

    /**
     * Handles audio focus when playing a sound file
     */
    private AudioManager mAudioManager;

    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SD_DATA_PATH = getApplicationContext().getFilesDir().getAbsolutePath();
        // Setup List of Words for the actual game
        getGameParams();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        countDownTextView = (TextView) findViewById(R.id.countdown_textView);
        countDowTextViewColor = (GradientDrawable) countDownTextView.getBackground();
        replayButton = (ImageButton) findViewById(R.id.replay_button);
        skipButton = (ImageButton) findViewById(R.id.skip_button);
        input = (CustomEditText) findViewById(R.id.editText);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Force keyboard to open
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        // Force All caps
        input.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        /* Creates Audio files directory */
        audioFileDirectory = new File(SD_DATA_PATH + File.separator + ".audio");
        if (!audioFileDirectory.mkdirs()) {
            Log.e("GameActivity", "Error with the audio file directory");
        }
        savingWords(randomWordsList);


        startCountdown();


    }

    /* Function to get the game parameters from the selected level */
    private void getGameParams() {
        Intent intent = getIntent();
        levelFile = intent.getStringExtra("levelString");

        /* Checks if the file name was retrieved */
        if (levelFile == null) {
            Log.v("GameActivity", "Error retrieving file name");
            levelFile = "grade1"; // set default
        }

        levelInt = intent.getIntExtra("level", 1);

        Read read = new Read(GameActivity.this, levelFile, levelInt);

        words = read.loadFile();

        randomWordsList = pickNRandom(words, 30);
        // Randomize the order of the Array
        Collections.shuffle(randomWordsList, new Random(System.nanoTime()));

    }

    @Override
    public void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        releaseMediaPlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        // If activity stopped also stop counter
        countDownTimer.cancel();
        releaseMediaPlayer();
        super.onStop();

    }

    public void playWord(String word) {
        // Create and setup the {@link AudioManager} to request audio focus
        mAudioManager = (AudioManager) GameActivity.this.getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus so in order to play the audio file. The app needs to play a
        // short audio file, so we will request audio focus with a short amount of time
        // with AUDIOFOCUS_GAIN_TRANSIENT.
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // We have audio focus now.

            // Create and setup the {@link MediaPlayer} for the audio resource associated
            // with the current word
            String audioFileDataPath = audioFileDirectory + File.separator + word + ".wav";

            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(audioFileDataPath);
                mMediaPlayer.prepare();
                // Start the audio file
                mMediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Setup a listener on the media player, so that we can stop and release the
            // media player once the sound has finished playing.
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
        }
    }

    public void savingWords(final ArrayList<Word> ArrayOfWords) {

        /** For loop that goes throw the whole list of words of the actual game and
         *  that save the synthesized spoken words in files
         */

        for (int i = 0; i < ArrayOfWords.size(); i++) {

            final String word = ArrayOfWords.get(i).getWord();
            // Set the file path for the actual word
            final String audioFileDataPath = audioFileDirectory + File.separator + word + ".wav";

            /*final File audioFile = new File(audioFileDataPath);*/
            // Initialize tts
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.UK);

                        tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "Try");
                        tts.synthesizeToFile(word, null, new File(audioFileDataPath), TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

                    }
                }
            });
            /*if (audioFile.exists()) {
                Log.d("savingWords", "successfully created fileTTS for "+word);
            }
            else {
                Log.d("savingWords", "failed while creating fileTTS for "+word);
            }*/
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    filesFinshedLoading();
                }

                @Override
                public void onError(String utteranceId) {

                }
            });

        }
    }

    private void filesFinshedLoading() {
        startCountdown();
    }

    private void startCountdown() {

        /* Initialize current word of word array
         * If first time called it plays first word of array
         * Play current word if first time called
         */
        playWord(randomWordsList.get(indexOfActualWord).getWord());

        // If the player hit the Replay Button
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Play the current word
                playWord(randomWordsList.get(indexOfActualWord).getWord());
            }
        });

        // Skip to next word in list if player hit skip button
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checks if the current word is last of list
                if (indexOfActualWord < (randomWordsList.size() - 1)) {
                    /* if word is not last of list increment indexOfWords by one
                     * so as to go to next word in array
                    */
                    indexOfActualWord++;
                    // set EditText field blank
                    input.setText("");
                    // playWord next word
                    playWord(randomWordsList.get(indexOfActualWord).getWord());

                } else // if we are in the end of list play current word again
                    playWord(randomWordsList.get(indexOfActualWord).getWord());
            }
        });


        // Set onClickListener on EditText field
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Get input of player of EditText field
                    randomWordsList.get(indexOfActualWord).setPlayerInput(input.getText().toString().trim().toLowerCase());

                    if (indexOfActualWord < randomWordsList.size() - 1) {
                        // Set entry filed blank
                        input.setText("");

                        // Force keyboard to stay open
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                        // Play next word in list
                        indexOfActualWord++;
                        playWord(randomWordsList.get(indexOfActualWord).getWord());
                    }
                    return true;
                } else
                    return false;
            }
        });

        // Debuging button
        Button resultsButton = (Button)findViewById(R.id.results_button);

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToResults();

            }
        });
        // End of debuging code

        /*// Start CountDownTimer
        countDownTimer = new CountDownTimer(60000, 1000) {


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
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                // Hide all the views exept the counter view
                input.setVisibility(View.GONE);
                replayButton.setVisibility(View.GONE);
                skipButton.setVisibility(View.GONE);

                // Set countdown TextView to done
                countDowTextViewColor.setColor(null);
                countDownTextView.setText("Done");

                // Trim the list to the actual numbers of entries of the player
                randomWordsList = new ArrayList<>(randomWordsList.subList(0, indexOfActualWord));

                *//*  Start the ResultActivity when the counter is finished  *//*
                Intent i = new Intent(GameActivity.this, ResultsActivity.class);
                i.putExtra("level", levelFile);
                startActivity(i);

                *//* Finish current Activity so as that the player can't come back on this activity
                 * when hitting the back button
                 *//*
                finish();
            }
        }.start();*/

    }

    private void goToResults() {
        // Trim the list to the actual numbers of entries of the player
        randomWordsList = new ArrayList<>(randomWordsList.subList(0, indexOfActualWord));

                /*  Start the ResultActivity when the counter is finished  */
        Intent i = new Intent(GameActivity.this, ResultsActivity.class);
        i.putExtra("level", levelFile);
        startActivity(i);

                /* Finish current Activity so as that the player can't come back on this activity
                 * when hitting the back button
                 */
        finish();
    }


    public static ArrayList<Word> pickNRandom(ArrayList<Word> lst, int n) {
        ArrayList<Word> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, n));
    }


    /* Custom AsyncTask that takes as parameter an ArrayList of Words */
    class LoadTTSData extends AsyncTask<ArrayList<Word>, Void, Void> {


        @Override
        protected Void doInBackground(ArrayList<Word>... params) {
            savingWords(params[0]);
            return null;
        }
    }

}


