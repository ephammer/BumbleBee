package com.thinkhodl.bumblebee.backend;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.ui.GameActivity;


class LevelViewHolder extends RecyclerView.ViewHolder {

    private View view;

    public LevelViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;

    }

    public void setLevelTitle(String levelTitle){
        TextView levelTitleTextView = view.findViewById(R.id.level_title_text_view);
        levelTitleTextView.setText(levelTitle);
    }

    public void setLevelLevel(String levelLevel){
        TextView levelLevelTextView = view.findViewById(R.id.level_level_text_view);
        levelLevelTextView.setText("lvl " + levelLevel);
    }

    public void setLevelDescription(String levelDescription){
        TextView levelDescriptionTextView = view.findViewById(R.id.level_description_text_view);
        levelDescriptionTextView.setText(levelDescription);
    }

}
