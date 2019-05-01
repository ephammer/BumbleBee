package com.thinkhodl.bumblebee.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.thinkhodl.bumblebee.Utils;
import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.backend.GameAdapter;
import com.thinkhodl.bumblebee.ui.EditProfileActivity;
import com.thinkhodl.bumblebee.ui.GameActivity;
import com.thinkhodl.bumblebee.ui.MainActivity;
import com.thinkhodl.bumblebee.ui.RecyclerItemClickListener;
import com.thinkhodl.bumblebee.ui.ResultsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thinkhodl.bumblebee.Utils.GAME_DATABASE;
import static com.thinkhodl.bumblebee.Utils.GAME_TIMESTAMP;
import static com.thinkhodl.bumblebee.Utils.GAME_USER_ID;

public class StatsFragment extends Fragment {
    private final String TAG = this.getClass().getName();

    @Nullable
    @BindView(R.id.games_recyclerView)
    RecyclerView mRecyclerView;

    // Firestore Database
    private FirebaseFirestore dataBase;
    private FirebaseUser user;

    private GameAdapter adapter;


    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

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

        Query query = dataBase.collection(GAME_DATABASE)
                .whereEqualTo(GAME_USER_ID,user.getUid())
                .orderBy(GAME_TIMESTAMP, Query.Direction.DESCENDING)
                ;

        FirestoreRecyclerOptions<Game> options = new FirestoreRecyclerOptions.Builder<Game>()
                .setQuery(query, Game.class)
                .build();

        adapter = new GameAdapter(options);
        mRecyclerView.setAdapter(adapter);

        // Set onClickListener
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView ,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
//                        Toast.makeText(getContext(),adapter.getItem(position).getTimestamp().toDate().toString(), Toast.LENGTH_SHORT).show();
                        //                        Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                        dataBase.collection(GAME_DATABASE)
                                .whereEqualTo(GAME_USER_ID, user.getUid())
                                .whereEqualTo(GAME_TIMESTAMP,adapter.getItem(position).getTimestamp())
//                                .orderBy(GAME_TIMESTAMP, Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                                Game lastGame = task.getResult().getDocuments().get(0).toObject(Game.class);
//                                                Toast.makeText(getContext(),document.getId(),Toast.LENGTH_LONG).show();
                                                Intent resultsActivity = new Intent(getContext() , ResultsActivity.class);
                                                resultsActivity.putExtra(Utils.GAME_ID,document.getId());

                                                startActivity(resultsActivity);

                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        ((MainActivity) getActivity()).setActionBarTitle("Game stats");

    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }


}
