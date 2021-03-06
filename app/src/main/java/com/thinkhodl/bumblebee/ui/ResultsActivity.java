package com.thinkhodl.bumblebee.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.Utils;
import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.backend.PlayedWordAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thinkhodl.bumblebee.Utils.GAME_DATABASE;
import static com.thinkhodl.bumblebee.Utils.GAME_ID;
import static com.thinkhodl.bumblebee.Utils.GAME_TIMESTAMP;
import static com.thinkhodl.bumblebee.Utils.GAME_USER_ID;
import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_DATABASE;
import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_TIMESTAMP;
import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_USER;
import static com.thinkhodl.bumblebee.Utils.WORD_LEVEL;

public class ResultsActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getName();

    // Firestore Database
    private FirebaseFirestore dataBase;
    private FirebaseUser user;

    private String mGameID;
    private int mLevel;

    @BindView(R.id.play_again_button)
    Button playAgainButton;

    @BindView(R.id.highscore_text_view)
    TextView highScoreTextView;

    @BindView(R.id.numberOfWrongWords_score_textview)
    TextView nbWrongTextView;

    @BindView(R.id.numberOfCorrectWords_score_textview)
    TextView nbCorrectTextView;

    @BindView(R.id.results_list)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ButterKnife.bind(this);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        //        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Results");


        // Force keyboard to close
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        Intent intent = getIntent();
        mLevel = intent.getIntExtra(WORD_LEVEL, -1);
        mGameID = intent.getStringExtra(GAME_ID);
        //        Toast.makeText(this,mGameID,Toast.LENGTH_LONG).show();

        // Get last played game and display scores
        getGame();

        if (mLevel == -1)
            playAgainButton.setVisibility(View.GONE);
        else
            playAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ResultsActivity.this, GameActivity.class);
                    i.putExtra(WORD_LEVEL, mLevel);
                    startActivity(i);
                    finish();
                }
            });
    }

    private void getGame() {

        // Cloud Firestore Instance
        if (dataBase == null)
            dataBase = FirebaseFirestore.getInstance();
        if (user == null)
            user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = dataBase.collection(GAME_DATABASE).document(mGameID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Game game = documentSnapshot.toObject(Game.class);
                loadScores(game);

            }
        });

        /*
        dataBase.collection(GAME_DATABASE)
                .whereEqualTo(GAME_USER_ID, user.getUid())
                .orderBy(GAME_TIMESTAMP, Query.Direction.DESCENDING).limit(1)
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
        */
    }

    private void checkHighscore(final int currentScore) {

        // Cloud Firestore Instance
        if (dataBase == null)
            dataBase = FirebaseFirestore.getInstance();
        if (user == null)
            user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = dataBase.collection(Utils.HIGHSCORE_DATABASE).document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (currentScore > (int) (long) document.get(Utils.HIGHSCORE_SCORE))
                            setNewHighScore(currentScore);
                    } else {
                        Log.d(TAG, "No such document");
                        setNewHighScore(currentScore);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void setNewHighScore(int newHighscore) {

        // Cloud Firestore Instance
        if (dataBase == null)
            dataBase = FirebaseFirestore.getInstance();
        if (user == null)
            user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> highscore = new HashMap<>();
        highscore.put(Utils.HIGHSCORE_SCORE, newHighscore);
        highscore.put(HIGHSCORE_USER, user.getDisplayName());
        highscore.put(HIGHSCORE_TIMESTAMP, new Timestamp(new Date()));

        dataBase.collection(HIGHSCORE_DATABASE).document(user.getUid())
                .set(highscore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void loadScores(Game lastGame) {

        // Set Highscore
        int highscore = 0;

        // Compute highscore
        for (int i = 0; i < lastGame.getPlayedWords().size(); i++) {
            highscore += lastGame.getPlayedWords().get(i).getScore();
        }
        highScoreTextView.append(' ' + String.valueOf(highscore) + " xp");

        if(mLevel!=-1)
            checkHighscore(highscore);

        // Set stats of game
        int intNbWrong = 0;
        int intNbCorrect = 0;
        for (int i = 0; i < lastGame.getPlayedWords().size(); i++) {
            if (lastGame.getPlayedWords().get(i).getResult())
                intNbCorrect++;
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

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_resulte, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_end:
                finish();
                break;
            case android.R.id.home:
                break;
        }

        return true;

    }
}