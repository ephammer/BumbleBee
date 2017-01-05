package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class GameChargerActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private String audioFolderDataPath;

    private ArrayList<Word> words;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_charger);


        // Initialize the data path to the internal memory
        String SD_DATA_PATH = getApplicationContext().getFilesDir().getAbsolutePath();

        // Initialize audio folder where the synthesized audio files are stored
        audioFolderDataPath = SD_DATA_PATH + File.separator + "./audio";
        File audioFolder = new File(audioFolderDataPath);

        // Verifies if folder was created
        if (audioFolder.exists() || audioFolder.mkdirs()) {
            synthesizeArrayListToFile(getApplicationContext(), words);
        } else
            Log.v(TAG, "Error while creating audioFile folder");

    }


    public void synthesizeArrayListToFile(final Context context, final ArrayList<Word> words) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);

//                    // Change speed rate of the textToSpeech engine
//                    textToSpeech.setSpeechRate((float)0.5);

                    /* For loop that goes trough the whole list
                     * The loop saves each Word in a synthesised audio file
                     */
                    for (int i = 0; i < words.size(); i++) {
                        // First create data path to specific word
                        String wordDataPath = audioFolderDataPath + File.separator
                                + words.get(i).getWord() + ".ogg";

                        File tmp = new File(wordDataPath);

                        if(tmp.length()==0)
                        {
                            // Start synthesise of audio file in OGG format
                            textToSpeech.synthesizeToFile(
                                    words.get(i).getWord(),
                                    null,
                                    tmp,
                                    words.get(i).getWord());
                        }
                    }
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        // Once All the files from the TTS are finished loading onDone is called
                        @Override
                        public void onDone(String utteranceId) {
                            if(words.get(words.size()-1).getWord().equals(utteranceId))
                                if (textToSpeech != null) {
                                    textToSpeech.stop();
                                    textToSpeech.shutdown();
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
