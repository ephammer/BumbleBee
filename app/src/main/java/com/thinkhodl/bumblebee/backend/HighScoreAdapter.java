package com.thinkhodl.bumblebee.backend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.thinkhodl.bumblebee.R;


import androidx.annotation.NonNull;


public class HighScoreAdapter extends FirestoreRecyclerAdapter<HighScore,HighScoreViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HighScoreAdapter(@NonNull FirestoreRecyclerOptions<HighScore> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HighScoreViewHolder highScoreViewHolder, int i, @NonNull HighScore highScore) {
        highScoreViewHolder.setHighScoreName(highScore.getUser());
        highScoreViewHolder.setHighScoreScore(String.valueOf(highScore.getScore()));
    }
    @NonNull
    @Override
    public HighScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore_item, parent, false);
        return new HighScoreViewHolder(view);
    }



}

