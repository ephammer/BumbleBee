package com.thinkhodl.bumblebee.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.backend.PlayedWordAdapter;

public class ResultsActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getName();

    // Firestore Database
    private FirebaseFirestore dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Force keyboard to close
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        // Get last played game and display scores
        getLastGame();

        Button playAgainButton = (Button)findViewById(R.id.play_again_button);
        playAgainButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(ResultsActivity.this,GameActivity.class);
                Intent intent = getIntent();
                int levelInt = intent.getIntExtra("level",1);
                i.putExtra("level",levelInt);
                startActivity(i);
                finish();
            }
        });
    }

    private void getLastGame(){

        // Cloud Firestore Instance
        dataBase = FirebaseFirestore.getInstance();

        dataBase.collection("games")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Game lastGame = task.getResult().getDocuments().get(0).toObject(Game.class);
                                loadScores(lastGame);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadScores(Game lastGame){

        // Set Highscore
        TextView highScoreTextView = (TextView)findViewById(R.id.highscore_text_view);
        int highscore = 0;

        // Compute highscore
        for (int i = 0; i < lastGame.getPlayedWords().size() ; i++) {
            highscore += lastGame.getPlayedWords().get(i).getScore();
        }
        highScoreTextView.append(String.valueOf(highscore));


        // Set stats of game
        TextView nbWrongTextView = (TextView)findViewById(R.id.numberOfWrongWords_score_textview);
        TextView nbCorrectTextView = (TextView)findViewById(R.id.numberOfCorrectWords_score_textview);
        int intNbWrong = 0;
        int intNbCorrect=0;
        for (int i = 0; i < lastGame.getPlayedWords().size() ; i++) {
            if(lastGame.getPlayedWords().get(i).getResult())
                intNbCorrect ++;
            else
                intNbWrong++;
        }
        nbWrongTextView.setText(String.valueOf(intNbWrong));
        nbCorrectTextView.setText(String.valueOf(intNbCorrect));

        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        PlayedWordAdapter adapter = new PlayedWordAdapter(
                ResultsActivity.this,
                lastGame.getPlayedWords());

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView)findViewById(R.id.results_list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);


    }

}