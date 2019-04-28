package com.thinkhodl.bumblebee.backend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.thinkhodl.bumblebee.R;

public class LevelAdapter extends FirestoreRecyclerAdapter<Level, LevelViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LevelAdapter(@NonNull FirestoreRecyclerOptions<Level> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LevelViewHolder levelViewHolder, int i, @NonNull Level level) {
        levelViewHolder.setLevelTitle(level.getTitle());
        levelViewHolder.setLevelLevel(String.valueOf(level.getLevel()));
        levelViewHolder.setLevelDescription(level.getDescription());
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_item, parent, false);
        return new LevelViewHolder(view);
    }

}
