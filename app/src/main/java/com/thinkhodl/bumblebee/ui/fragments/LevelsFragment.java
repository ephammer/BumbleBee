package com.thinkhodl.bumblebee.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.backend.HighScore;
import com.thinkhodl.bumblebee.backend.HighScoreAdapter;
import com.thinkhodl.bumblebee.backend.Level;
import com.thinkhodl.bumblebee.backend.LevelAdapter;
import com.thinkhodl.bumblebee.backend.OnLevelClickListener;
import com.thinkhodl.bumblebee.ui.GameActivity;
import com.thinkhodl.bumblebee.ui.LevelChoiceActivity;
import com.thinkhodl.bumblebee.ui.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_DATABASE;
import static com.thinkhodl.bumblebee.Utils.HIGHSCORE_SCORE;
import static com.thinkhodl.bumblebee.Utils.LEVEL_DATABASE;
import static com.thinkhodl.bumblebee.Utils.LEVEL_LEVEL;

public class LevelsFragment extends Fragment {
    private final String TAG = this.getClass().getName();

    @Nullable
    @BindView(R.id.levels_recyclerView)
    RecyclerView mRecyclerView;

    // Firestore Database
    private FirebaseFirestore dataBase;
    private FirebaseUser user;

    private LevelAdapter adapter;


    public LevelsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Defines the xml file for the fragment
        View rootView = inflater.inflate(R.layout.fragment_levels, container, false);

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

        Query query = dataBase.collection(LEVEL_DATABASE)
                .orderBy(LEVEL_LEVEL, Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Level> options = new FirestoreRecyclerOptions.Builder<Level>()
                .setQuery(query, Level.class)
                .build();

        adapter = new LevelAdapter(options);

        mRecyclerView.setAdapter(adapter);

        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle("Levels");
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }


}
