package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class GameLevelActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private String audioFolderDataPath;

    // UI elements
    private ImageView beeSpiner;
    private ListView listView;
    private Animation rotation;
    /* Function that takes as input a ArrayList of Words
     * shuffles the list and randomizes the list */
    public static ArrayList<Word> pickNRandom(ArrayList<Word> lst, int n) {
        ArrayList<Word> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, n));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level);

        beeSpiner = (ImageView) findViewById(R.id.animation_bee);
        beeSpiner.setVisibility(View.INVISIBLE);


        final ArrayList<Level> levelArrayList = new ArrayList<>();
        levelArrayList.add(new Level("Level 1", 1, R.drawable.bee_level_1));
        levelArrayList.add(new Level("Level 2", 2, R.drawable.bee_level_2));
        levelArrayList.add(new Level("Level 3", 3, R.drawable.bee_level_3));
        levelArrayList.add(new Level("Level 4", 4, R.drawable.bee_level_4));
        levelArrayList.add(new Level("Level 5", 5, R.drawable.bee_icon));

        LevelAdapter levelAdapter = new LevelAdapter(getApplicationContext(), levelArrayList);

        listView = (ListView) findViewById(R.id.game_level_list_view);
        listView.setAdapter(levelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Everything on screen disappears and the bee spinner appears
                listView.setVisibility(View.GONE);
                beeSpiner.setVisibility(View.VISIBLE);
                // Initialize the animation and start the animation
                rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
                beeSpiner.startAnimation(rotation);

                new LoadTTS(getApplicationContext(), configureListOfWords(getFile(position),levelArrayList.get(position).getLevelInt()));

//                final Intent intent = new Intent(getApplicationContext(), GameActivity.class);
//                intent.putExtra("levelString", getFile(position));
//                intent.putExtra("levelInt", levelArrayList.get(position).getLevelInt());
//
//                // Everything on screen disappears and the bee spinner appears
//                listView.setVisibility(View.GONE);
//                beeSpiner.setVisibility(View.VISIBLE);
//
//                // Initialize the animation and start the animation
//                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
//                beeSpiner.startAnimation(rotation);
//
//                rotation.setAnimationListener(new Animation.AnimationListener() {
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//
//                    public void onAnimationEnd(Animation animation) {
//                        startActivity(intent);
//                        finish();
//                    }
//                });

            }


        });

    }

    private String getFile(int position) {
        switch (position) {
            case 0:
                return "grade1";
            case 1:
                return "grade2";
            case 2:
                return "grade3";
            case 3:
                return "grade4";
            case 4:
                return "grade5";
        }
        return null;
    }

    /* Function to configure the played Array */
    private ArrayList<Word> configureListOfWords(String levelFile, int levelInt)
    {
        ArrayList<Word> words;

        // Get all the words contained in the level file
        Read read = new Read(GameLevelActivity.this, levelFile, levelInt);

        // Load the words in an ArrayList of Words
        words = read.loadFile();

        // Narrow the number of Words down to 30 and randomize the list
        words = pickNRandom(words, 30);

        return words;
    }

    private class LoadTTS {

        private TextToSpeech textToSpeech;
        private String audioFolderDataPath;
        private String SD_DATA_PATH;

        public LoadTTS(Context context, ArrayList<Word> words) {
            // Initialize the data path to the internal memory
            SD_DATA_PATH = context.getFilesDir().getAbsolutePath();

            // Initialize audio folder where the synthesized audio files are stored
            audioFolderDataPath = SD_DATA_PATH + File.separator + "audio";
            File audioFolder = new File(audioFolderDataPath);

            // Verifies if folder was created
            if (audioFolder.exists() || audioFolder.mkdirs()) {
                synthesizeArrayListToFile(context, words);
            } else
                Log.v(TAG, "Error while creating audioFile folder");

        }

        public void synthesizeArrayListToFile(final Context context, final ArrayList<Word> words) {
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);

                        for (int i = 0; i < words.size(); i++) {
                            String wordDataPath = audioFolderDataPath + File.separator
                                    + words.get(i).getWord() + ".ogg";

                            // Start synthesise of audio file in WAV format
                            Log.v("Synthesize", words.get(i).getWord());
                            if (textToSpeech.synthesizeToFile(
                                    words.get(i).getWord(),
                                    null,
                                    new File(wordDataPath),
                                    words.get(i).getWord()
                            ) == TextToSpeech.SUCCESS)
                                Log.v("Queuing", "Success queuing " + words.get(i));
                        }


                        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {

                            }

                            // Once All the files from the TTS are finished loading onDone is called
                            @Override
                            public void onDone(String utteranceId) {

                                if (words.get(words.size()-1).getWord().equals(utteranceId)) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Initialize Intent
                                            Intent intent = new Intent(getApplicationContext(), GameFileActivity.class);
                                            intent.putParcelableArrayListExtra("Words", words);
                                            intent.putExtra("sdDataPath",SD_DATA_PATH);
                                            // Start next activity with intent
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                    if (textToSpeech != null) {
                                        textToSpeech.stop();
                                        textToSpeech.shutdown();
                                    }
                                }

                            }

                            @Override
                            public void onError(String utteranceId) {
                                Log.v(TAG, "Error during synthesise of files");
                            }
                        });
                    }
                }
            });

        }
    }
}
