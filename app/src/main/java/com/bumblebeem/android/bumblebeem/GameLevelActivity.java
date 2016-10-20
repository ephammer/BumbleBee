package com.bumblebeem.android.bumblebeem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class GameLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level);
        final ImageView beeSpiner = (ImageView) findViewById(R.id.animation_bee);
        beeSpiner.setVisibility(View.INVISIBLE);


        final ArrayList<Level> levelArrayList = new ArrayList<>();
        levelArrayList.add(new Level("Level 1", 1, R.drawable.bee_level_1));
        levelArrayList.add(new Level("Level 2", 2, R.drawable.bee_level_2));
        levelArrayList.add(new Level("Level 3", 3, R.drawable.bee_level_3));
        levelArrayList.add(new Level("Level 4", 4, R.drawable.bee_level_4));
        levelArrayList.add(new Level("Level 5", 5, R.drawable.bee_icon));

        LevelAdapter levelAdapter = new LevelAdapter(getApplicationContext(), levelArrayList);

        final ListView listView = (ListView) findViewById(R.id.game_level_list_view);
        listView.setAdapter(levelAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("levelString", getFile(position));
                intent.putExtra("levelInt", levelArrayList.get(position).getLevelInt());

                // Everything on screen disappears and the bee spinner appears
                listView.setVisibility(View.GONE);
                beeSpiner.setVisibility(View.VISIBLE);

                // Initialize the animation and start the animation
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
                beeSpiner.startAnimation(rotation);

                rotation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        startActivity(intent);
                        finish();
                    }
                });

            }

            private String getFile(int position) {
                switch (position) {
                    case 0:
                        return "grade1";
                    case 1:
                        return "grade2";
                    case 2:
                        return "grade3";
                    case 3:
                        return "grade4";
                    case 4:
                        return "grade5";
                }
                return null;
            }
        });

    }
}
