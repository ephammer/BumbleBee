package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by ephammer on 21/10/16.
 */

public class LoadTTS {

    private TextToSpeech textToSpeech;
    private String SD_DATA_PATH;
    private String audioFolderDataPath;

    // Boolean that verifies stat of TTS
    private boolean loadingFinished = false;


    public LoadTTS(Context context, ArrayList<Word> words) {
        // Initialize the data path to the internal memory
        SD_DATA_PATH = context.getFilesDir().getAbsolutePath();

        // Initialize audio folder where the synthesized audio files are stored
        audioFolderDataPath = SD_DATA_PATH + File.separator + "./audio";
        File audioFolder = new File(audioFolderDataPath);

        // Verifies if folder was created
        if (audioFolder.exists() || audioFolder.mkdirs()) {
            synthesizeArrayListToFile(context, words);
        } else
            Log.v(TAG, "Error while creating audioFile folder");

    }

    public void synthesizeArrayListToFile(Context context, final ArrayList<Word> words) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);

                    /* For loop that goes trough the whole list
                     * The loop saves each Word in a synthesised audio file
                     */
                    for (int i = 0; i < words.size(); i++) {
                        // First create data path to specific word
                        String wordDataPath = audioFolderDataPath + File.separator
                                + words.get(i).getWord() + ".wav";

                        // Start synthesise of audio file in WAV format
                        textToSpeech.synthesizeToFile(
                                words.get(i).getWord(),
                                null,
                                new File(wordDataPath),
                                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                    }
                }
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                loadingFinished = false;
            }

            // Once All the files from the TTS are finished loading onDone is called
            @Override
            public void onDone(String utteranceId) {
                loadingFinished = true;
            }

            @Override
            public void onError(String utteranceId) {
                loadingFinished = false;
                Log.v(TAG, "Error during synthesise of files");
            }
        });
    }

    public boolean isLoadingFinished() {
        return loadingFinished;
    }
}
