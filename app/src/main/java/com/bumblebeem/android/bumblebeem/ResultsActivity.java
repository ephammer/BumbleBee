package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Force keyboard to close
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        // Set custom font to Result Text View
        TextView results = (TextView)findViewById(R.id.results_text_view);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/pacifico.ttf");
        results.setTypeface(custom_font);

        // Set Highscore
        TextView highScoreTextView = (TextView)findViewById(R.id.highscore_text_view);
        int highscore = 0;

        // Compute highscore
        for (int i = 0; i < GameActivity.randomWordsList.size() ; i++) {
            highscore += GameActivity.randomWordsList.get(i).score();
        }
        highScoreTextView.append(String.valueOf(highscore));
        highScoreTextView.setTypeface(custom_font);

        // Set stats of game
        TextView nbWrongTextView = (TextView)findViewById(R.id.numberOfWrongWords_score_textview);
        TextView nbCorrectTextView = (TextView)findViewById(R.id.numberOfCorrectWords_score_textview);
        int intNbWrong = 0;
        int intNbCorrect=0;
        for (int i = 0; i < GameActivity.randomWordsList.size() ; i++) {
            if(GameActivity.randomWordsList.get(i).isEqual())
                intNbCorrect ++;
            else
                intNbWrong++;
        }
        nbWrongTextView.setText(String.valueOf(intNbWrong));
        nbCorrectTextView.setText(String.valueOf(intNbCorrect));

        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        WordAdapter adapter = new WordAdapter(ResultsActivity.this, GameActivity.randomWordsList);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView)findViewById(R.id.results_list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);

        Button playAgainButton = (Button)findViewById(R.id.play_again_button);
        playAgainButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(ResultsActivity.this,GameActivity.class);
                Intent intent = getIntent();
                String levelFile = intent.getStringExtra("level");
                i.putExtra("level",levelFile);
                startActivity(i);
                finish();
            }
        });
    }

}