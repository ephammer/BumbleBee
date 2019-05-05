package com.thinkhodl.bumblebee.ui.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.backend.HighScore;
import com.thinkhodl.bumblebee.backend.HighScoreAdapter;
import com.thinkhodl.bumblebee.backend.HighScoreViewHolder;
import com.thinkhodl.bumblebee.ui.MainActivity;

import java.util.ArrayList;

import static com.thinkhodl.bumblebee.Utils.GAME_DATABASE;
import static com.thinkhodl.bumblebee.Utils.GAME_TIMESTAMP;
import static com.thinkhodl.bumblebee.Utils.GAME_USER_ID;
import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_DATABASE;
import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_SCORE;


public class RankingFragment extends Fragment {

    private final String TAG = this.getClass().getName();

    @Nullable
    @BindView(R.id.ranking_recyclerView)
    RecyclerView mRecyclerView;

    // Firestore Database
    private FirebaseFirestore dataBase;
    private FirebaseUser user;

    private HighScoreAdapter adapter;


    public RankingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Defines the xml file for the fragment
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        ButterKnife.bind(this, rootView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Cloud Firestore Instance
        if(dataBase==null)
            dataBase = FirebaseFirestore.getInstance();
        if(user==null)
            user = FirebaseAuth.getInstance().getCurrentUser();

        Query query = dataBase.collection(HIGHSCORE_DATABASE)
                .orderBy(HIGHSCORE_SCORE, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HighScore> options = new FirestoreRecyclerOptions.Builder<HighScore>()
                .setQuery(query, HighScore.class)
                .build();

        adapter = new HighScoreAdapter(options);
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle("Game rankings");
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }


}
