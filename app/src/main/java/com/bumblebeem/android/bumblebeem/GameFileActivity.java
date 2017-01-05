package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameFileActivity extends AppCompatActivity {

    // ArrayList of Words that contains the words of the actual game
    public static ArrayList<Word> words;
    private static String SD_DATA_PATH;
    // Initialize the index of the current Word that is played in the list of Words
    private int indexOfActualWord = 0;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize AudioManager
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);


        /* First we get the parameters for the configuration for the actual game
         * The parameters are send over by the last Activity as Intent Extras.
         * The ArrayList of Words is passed */
        Bundle bundle = getIntent().getExtras();
        words = bundle.getParcelableArrayList("Words");


        // Initialize SD_DATA_PATH
        SD_DATA_PATH = getApplicationContext().getFilesDir().getAbsolutePath();

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
                    countDowTextViewColor.setColor(ContextCompat.getColor(GameFileActivity.this, R.color.circle2));

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
                Intent i = new Intent(GameFileActivity.this, ResultsActivity.class);
                i.putParcelableArrayListExtra("Words", words);
                startActivity(i);

                /* Finish current Activity so as that the player can't come back on this activity
                 * when hitting the back button
                 */
                finish();
            }
        }.start();

        // Function that plays the actual word

    }

    private void playWord(String word) {
        // Release the media player if it currently exists because we are about to

        // play a different sound file
        releaseMediaPlayer();

        // Request audio focus so in order to play the audio file. The app needs to play a
        // short audio file, so we will request audio focus with a short amount of time
        // with AUDIOFOCUS_GAIN_TRANSIENT.
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // We have audio focus now.

            // Create and setup the {@link MediaPlayer} for the audio resource associated
            // with the current word
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(SD_DATA_PATH + File.separator + "audio" + File.separator + word + ".ogg");
                mMediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Start the audio file
            mMediaPlayer.start();

            // Setup a listener on the media player, so that we can stop and release the
            // media player once the sound has finished playing.
            mMediaPlayer.setOnCompletionListener(mCompletionListener);

        }
    }


    @Override
    public void onPause() {
        releaseMediaPlayer();
        super.onPause();
    }

    @Override
    public void onStop() {
        releaseMediaPlayer();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
    }
}

