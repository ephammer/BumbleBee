package com.thinkhodl.bumblebee.ui;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.backend.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.thinkhodl.bumblebee.Utils.WORD_LEVEL;
import static com.thinkhodl.bumblebee.Utils.WORD_WORD;

public class GameActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    // Firestore Database
    private FirebaseFirestore dataBase;

    // Local List of Words
    private ArrayList<Word> mWordList;

    // TextToSpeech engine
    private TextToSpeech textToSpeech;

    // Initialize the index of the current Word that is played in the list of Words
    private int indexOfActualWord = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Cloud Firestore Instance
        dataBase = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        int level = intent.getIntExtra("level",1);

        loadGame(level);
    }

    private void loadGame(int level) {

        mWordList = new ArrayList<>();

        dataBase.collection("words")
                .whereEqualTo("level",level)
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
                                                (int) document.get(WORD_LEVEL),
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
        mWordList = pickNRandom(mWordList,30);

        // Initialize the input method
        final InputMethodManager keyboard = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);


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
}
