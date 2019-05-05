package com.thinkhodl.bumblebee.backend;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.thinkhodl.bumblebee.R;

public class GameViewHolder extends RecyclerView.ViewHolder {

    private View view;

    public GameViewHolder( View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setLevel(String level){
        TextView levelTextView = view.findViewById(R.id.level_text_view);
        levelTextView.setText("Level: " + level);
    }

    public void setTimestamp(String timestamp){
        TextView timestampTextView = view.findViewById(R.id.timestamp_text_view);
        timestampTextView.setText(timestamp);
    }

    public void setNbCorrectWords(String nbCorrectWords){
        TextView nbCorrectWordsTextView = view.findViewById(R.id.nb_correct_played_words_text_view);
        nbCorrectWordsTextView.setText(nbCorrectWords);
    }

    public void setNbWrongtWords(String nbWrongtWords){
        TextView nbWrongWordsTextView = view.findViewById(R.id.nb_wrong_played_words_text_view);
        nbWrongWordsTextView.setText(nbWrongtWords);
    }

    public void setTotalWords(String totalWords){
        TextView totalWordsTextView = view.findViewById(R.id.nb_total_played_words_text_view);
        totalWordsTextView.setText(totalWords);
    }

    public void setScore(String score){
        TextView scoreTextView = view.findViewById(R.id.game_score_text_view);
        scoreTextView.setText(score + " xp");
    }
}
