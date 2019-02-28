package com.thinkhodl.bumblebee.ui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thinkhodl.bumblebee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LevelChoiceActivity extends AppCompatActivity {

    @BindView(R.id.level_1_button)
    Button mLevelOneButton;

    @BindView(R.id.level_2_button)
    Button mLevelTwoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_choice);

        ButterKnife.bind(this);

        mLevelOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent levelOneIntent = new Intent(LevelChoiceActivity.this ,GameActivity.class);
                levelOneIntent.putExtra("level", 1);
                startActivity(levelOneIntent);
            }
        });

        mLevelTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent levelTwoIntent = new Intent(LevelChoiceActivity.this ,GameActivity.class);
                levelTwoIntent.putExtra("level", 2);
                startActivity(levelTwoIntent);
            }
        });
    }
}
