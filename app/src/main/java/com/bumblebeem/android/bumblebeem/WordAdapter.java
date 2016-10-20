package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by n3v10t on 18/09/16.
 */
public class WordAdapter extends ArrayAdapter<Word>
{

    public WordAdapter(Context context, ArrayList<Word> list)
    {
        super(context,0, list);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of words.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.word_item, parent, false);
        }

        // Find the earthquake at the given position in the list of earthquakes
        Word currentWord = getItem(position);

        // Set actual word
        TextView word = (TextView)listItemView.findViewById(R.id.actual_word);
        word.setText(currentWord.getWord());

        // Set player input word
        TextView inputWord = (TextView)listItemView.findViewById(R.id.player_input);
        inputWord.setText(currentWord.getPlayerInput());

        TextView score = (TextView)listItemView.findViewById(R.id.score_textview);
        score.setText(String.valueOf(currentWord.score()) + " xp");

        if(currentWord.isEqual())
        {
            inputWord.setTextColor(ContextCompat.getColor(getContext(), R.color.green));

//            // Rearrange the item to only display the word
//            word.setVisibility(View.GONE);
//            TextView wordTitle = (TextView)listItemView.findViewById(R.id.actual_word_title);
//            wordTitle.setVisibility(View.GONE);

        }
        else {
            inputWord.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            ImageView beeIcon = (ImageView)listItemView.findViewById(R.id.imageView);
            beeIcon.setImageResource(R.drawable.bee_sad_icon);
            beeIcon.setBackgroundResource(R.drawable.honeycomb_wrong_icon);
        }

        // Set rounded corner shape around each item
        listItemView.setBackgroundResource(R.drawable.custom_shape);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
