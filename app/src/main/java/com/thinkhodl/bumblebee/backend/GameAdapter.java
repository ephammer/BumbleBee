package com.thinkhodl.bumblebee.backend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.thinkhodl.bumblebee.R;

public class GameAdapter extends FirestoreRecyclerAdapter<Game, GameViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GameAdapter(@NonNull FirestoreRecyclerOptions<Game> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GameViewHolder gameViewHolder, int i, @NonNull Game game) {
        gameViewHolder.setLevel(String.valueOf(game.getPlayedWords().get(0).getLevel()));
        if(game.getTimestamp()!=null)
            gameViewHolder.setTimestamp(game.getTimestamp().toDate().toString());
        gameViewHolder.setScore(String.valueOf(game.getTotalScore()));
        gameViewHolder.setTotalWords(String.valueOf(game.getPlayedWords().size()));

        int nbCorrectWords = 0;
        int nbWrongWords = 0;
        for(int j = 0; j < game.getPlayedWords().size(); j++){
            if(game.getPlayedWords().get(j).getResult())
                nbCorrectWords++;
            else
                nbWrongWords++;
        }

        gameViewHolder.setNbCorrectWords(String.valueOf(nbCorrectWords));
        gameViewHolder.setNbWrongtWords(String.valueOf(nbWrongWords));

    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item,parent, false);
        return new GameViewHolder(view);
    }
}
