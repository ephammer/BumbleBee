package com.thinkhodl.bumblebee.backend;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinkhodl.bumblebee.R;

import androidx.recyclerview.widget.RecyclerView;

public class HighScoreViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public HighScoreViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setHighScoreName(String highScoreName){
        TextView name = view.findViewById(R.id.highscore_name_text_view);
        name.setText(highScoreName);
    }

    public void setHighScoreScore(String highScore){
        TextView scoreTextView = view.findViewById(R.id.highscore_score_text_view);
        scoreTextView.setText(highScore + " xp");
    }

    public void setHighScoreIcon(int position){
        ImageView icon = view.findViewById(R.id.highscore_icon);
        switch (position){
            case 1:
                icon.setImageResource(R.drawable.ic_best_medal);
                break;
            case 2:
                icon.setImageResource(R.drawable.ic_second_medal);
                break;
            case 3:
                icon.setImageResource(R.drawable.ic_third_medal);
                break;
            default:
                icon.setVisibility(View.INVISIBLE);
                break;

        }
    }
}
