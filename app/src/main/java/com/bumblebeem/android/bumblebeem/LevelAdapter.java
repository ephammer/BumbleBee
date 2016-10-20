package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by n3v10t on 07/10/16.
 */

public class LevelAdapter extends ArrayAdapter<Level> {
    public LevelAdapter(Context context, ArrayList<Level> list)
    {
        super(context,0, list);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of levels.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.level_item, parent, false);
        }

        Level currentLevel = getItem(position);

        // Set actual level
        TextView level = (TextView)listItemView.findViewById(R.id.level_text_view);
        level.setText(currentLevel.getLevel());

        // Set Image
        ImageView imageIcon = (ImageView)listItemView.findViewById(R.id.level_item_image_view);
        imageIcon.setImageResource(currentLevel.getImageRes());


        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
