package com.thinkhodl.bumblebee.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.Utils;
import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.backend.PlayedWord;
import com.thinkhodl.bumblebee.backend.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static com.thinkhodl.bumblebee.Utils.GAME_DATABASE;
import static com.thinkhodl.bumblebee.Utils.WORD_DATABASE;
import static com.thinkhodl.bumblebee.Utils.WORD_LEVEL;
import static com.thinkhodl.bumblebee.Utils.WORD_WORD;

public class GameActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    // Firestore Database
    private FirebaseFirestore dataBase;

    // Local List of Words
    private ArrayList<Word> mWordList;

    // User input words
    private  ArrayList<PlayedWord> mPlayedWordList;

    // TextToSpeech engine
    private TextToSpeech textToSpeech;

    // Initialize the index of the current Word that is played in the list of Words
    private int indexOfActualWord = 0;

    // Keyboard
    private InputMethodManager keyboard;

    // Game
    private Game mGame = new Game();

    // Level
    private int mLevel;

    // Countdown Timer
    CountDownTimer countDownTimer;

    /*
     * UI elements
     */
    @BindView(R.id.game_loading_progressBar)
    ProgressBar mGameLoadingProgessBar;

    @BindView(R.id.countdown_textView)
    TextView mCountdownTexTView;

    @BindView(R.id.editText)
    EditText mEditText;

    @BindView(R.id.skip_button)
    Button mSkipButton;

    @BindView(R.id.replay_button)
    Button mReplayButton;

    @BindView(R.id.game_linear_layout)
    LinearLayout mGameLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Bind Views
        ButterKnife.bind(this);

        // Cloud Firestore Instance
        dataBase = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        mLevel = intent.getIntExtra(WORD_LEVEL,1);

        loadGame(mLevel);
    }

    private void loadGame(int level) {

        mWordList = new ArrayList<>();

        dataBase.collection(WORD_DATABASE)
                .whereEqualTo(WORD_LEVEL,level)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mWordList.add(
                                        new Word(
                                                document.getId(),
                                                Math.toIntExact((long)document.get(WORD_LEVEL)),
                                                (String) document.get(WORD_WORD)));
                            }
                            startGame();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void startGame() {

        // Randomize the Word list
        int sizeWordList = mWordList.size();
        if(sizeWordList<30)
            mWordList = pickNRandom(mWordList,sizeWordList);
        else
            mWordList = pickNRandom(mWordList,30);

        // Initialize word list for user
        mPlayedWordList = new ArrayList<>();

        // Set UI elements
        mGameLoadingProgessBar.setVisibility(View.GONE);
        mGameLinearLayout.setVisibility(View.VISIBLE);

        mEditText.requestFocus();
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
        showSoftKeyboard(mEditText);

        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playWord(mWordList.get(indexOfActualWord).getWord());
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks if the current word is last of list
                if (indexOfActualWord < (mWordList.size() - 1)) {
                    /* if word is not last of list increment indexOfWords by one
                     * so as to go to next word in array
                     */
                    indexOfActualWord++;
                    // set EditText field blank
                    mEditText.setText("");
                    // playWord on next word in List
                    playWord(mWordList.get(indexOfActualWord).getWord());

                } else // if we are in the end of list play current word again
                    playWord(mWordList.get(indexOfActualWord).getWord());
            }
        });

        // Force All caps in the EditText field
        mEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        /* Set OnEditorActionListener on the EditText field
         * The listener listens if the user enter the ENTER key
         * When activated the content of the EditText field is saved in the corresponding
         * playerInput field in the actual word */
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Get input of player of EditText field
                    String playerImput = mEditText.getText().toString().trim().toLowerCase();

                    // Check if users input is not null
                    if(playerImput.length()>0){
                        mPlayedWordList.add(new PlayedWord(
                                mWordList.get(indexOfActualWord),playerImput));


                        if (indexOfActualWord < mWordList.size() - 1) {
                            // Set entry filed blank
                            mEditText.setText("");

                            // Force keyboard to stay open
                            keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                    InputMethodManager.HIDE_IMPLICIT_ONLY);

                            // Play next word in list
                            indexOfActualWord++;
                            playWord(mWordList.get(indexOfActualWord).getWord());
                        }
                    }
                    showSoftKeyboard(mEditText);
                    return true;
                } else
                    return false;
            }
        });

        // Start with the first word of the list
        playWord(mWordList.get(indexOfActualWord).getWord());


        // Start CountDownTimer
        countDownTimer = new CountDownTimer(60000, 1000) {


            @Override
            public void onTick(long l) {
                mCountdownTexTView.setText(String.valueOf(l / 1000));

                if (l < 6000) {
                    mCountdownTexTView.setTextColor(Color.RED);

                }
            }

            @Override
            public void onFinish() {
                /*

                // Hide keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                // Hide all the views exept the counter view
                mEditText.setVisibility(View.GONE);
                mReplayButton.setVisibility(View.GONE);
                mSkipButton.setVisibility(View.GONE);

                // Set countdown TextView to done
                mCountdownTexTView.setText(R.string.done_string);
*/

                /*
                //  Start the ResultActivity when the counter is finished
                Intent i = new Intent(GameActivity.this, ResultsActivity.class);
                i.putExtra("level", levelInt);
                i.putExtra("levelString", levelFile );
                startActivity(i);
                 // Finish current Activity so as that the player can't come back on this activity
                 // when hitting the back button

                finish();
                */

                endGame();

            }
        }.start();

    }

    private void endGame(){

        // Release TTS engine
        relaseTTS();

        // Hide keyboard
        keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        // Hide Game layout and show Loading progressbar
        mGameLinearLayout.setVisibility(View.GONE);
        mGameLoadingProgessBar.setVisibility(View.VISIBLE);


        /*
        // Hide all the views exept the counter view
        mEditText.setVisibility(View.GONE);
        mReplayButton.setVisibility(View.GONE);
        mSkipButton.setVisibility(View.GONE);
        // Set countdown TextView to done
        mCountdownTexTView.setText(R.string.done_string);
        */
        if(mPlayedWordList.size()!=0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mGame.setUserID(user.getUid());
            mGame.setPlayedWords(mPlayedWordList);
            mGame.setDate(new Timestamp(new Date()));
            dataBase.collection(GAME_DATABASE)
                    .add(mGame)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            Intent resultsActivity = new Intent(GameActivity.this, ResultsActivity.class);
                            resultsActivity.putExtra(Utils.WORD_LEVEL, mLevel);
                            startActivity(resultsActivity);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            finish();
                        }
                    });
        }
        else
            finish();

    }

    // Function that plays the actual word
    private void playWord(final String word) {
        if(textToSpeech==null) {
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
        else
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    /* Function that takes as input a ArrayList of Words
     * shuffles the list and randomizes the list */
    public static ArrayList<Word> pickNRandom(ArrayList<Word> lst, int n) {
        ArrayList<Word> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, n));
    }

    private void relaseTTS()
    {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        relaseTTS();

    }

    @Override
    public void onStop() {
        super.onStop();
        relaseTTS();
        stopCountdown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        relaseTTS();
        stopCountdown();
    }

    public void stopCountdown(){
        if(countDownTimer!=null)
            countDownTimer.cancel();

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this,R.style.CustomDialogue)
                //                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Ending game")
                .setMessage("Are you sure you want to end this game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
