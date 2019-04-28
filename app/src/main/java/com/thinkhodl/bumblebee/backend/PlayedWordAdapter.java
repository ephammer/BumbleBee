package com.thinkhodl.bumblebee.backend;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinkhodl.bumblebee.R;

import java.util.ArrayList;

public class PlayedWordAdapter extends ArrayAdapter<PlayedWord> {
    public PlayedWordAdapter(Context context, ArrayList<PlayedWord> list)
    {
        super(context,0, list);
    }

    /**
     * Returns a list item view that displays information at the given position
     * in the list of words.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.player_word_item, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        PlayedWord currentWord = getItem(position);

        // Set actual word
        TextView word = listItemView.findViewById(R.id.actual_word);
        word.setText(currentWord.getWord());

        // Set player input word
        TextView inputWord = listItemView.findViewById(R.id.player_input);
        inputWord.setText(currentWord.getUserInput());

        TextView score = (TextView)listItemView.findViewById(R.id.score_textview);
        score.setText(String.valueOf(currentWord.getScore()) + " xp");

        if(currentWord.getResult())
            inputWord.setTextColor(Color.GREEN);
        else {
            inputWord.setTextColor(Color.RED);
            ImageView beeIcon = listItemView.findViewById(R.id.user_avatar_imageView);
        }

        // Set rounded corner shape around each item
//        listItemView.setBackgroundResource(R.drawable.custom_shape);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
