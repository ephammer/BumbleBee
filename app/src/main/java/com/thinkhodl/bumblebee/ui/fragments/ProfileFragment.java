package com.thinkhodl.bumblebee.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.AnyChartView;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.backend.Game;
import com.thinkhodl.bumblebee.ui.EditProfileActivity;
import com.thinkhodl.bumblebee.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thinkhodl.bumblebee.Utils.GAME_DATABASE;
import static com.thinkhodl.bumblebee.Utils.GAME_TIMESTAMP;
import static com.thinkhodl.bumblebee.Utils.GAME_USER_ID;

public class ProfileFragment extends Fragment {
    private final String TAG = this.getClass().getName();


    // Firestore Database
    private FirebaseFirestore dataBase;
    private FirebaseUser user;

    @BindView(R.id.email_profile_textView)
    TextView mEmailTextView;

    @BindView(R.id.name_profile_textView)
    TextView mNameTextView;

    @BindView(R.id.avatar_profile_imageView)
    ImageView mAvatarImageView;

    @BindView(R.id.total_number_games_textView)
    TextView mTotalNumberGames;

//    @BindView(R.id.mChartXP)
    AnyChartView anyChartView;

    @BindView(R.id.linechart2)
    LineChart mChartXP;

    @BindView(R.id.linechart3)
    LineChart mChartNbWords;

    @BindView(R.id.total_number_words_textView)
    TextView mTotalNumberWordsTextView;

    @BindView(R.id.total_number_correct_words_textView)
    TextView mTotalCorrectWordsTextView;

    @BindView(R.id.total_number_wrong_words_textView)
    TextView mTotalWrongWordsTextView;

    @BindView(R.id.profile_stats_linear_layout)
    LinearLayout mProfileStatLinearLayout;

    @BindView(R.id.profile_stats_loading)
    ProgressBar mProfileStatsLoading;

    @BindView(R.id.edit_profile_button)
    ImageButton mEditProfileButton;

    @BindView(R.id.pieChart)
    PieChart pieChart;

    private Context mContext;

    private int mTotalGamesTotalNb = 0;
    private int mTotalGamesTotalCorrectNb = 0;
    private int mTotalGamesTotalWrongNb = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Defines the xml file for the fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, rootView);


        mContext = getContext();

        // Cloud Firestore Instance
        if(dataBase==null)
            dataBase = FirebaseFirestore.getInstance();
        if(user==null)
            user = FirebaseAuth.getInstance().getCurrentUser();

        loadProfileInfo();
        getGameStats();

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext , EditProfileActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    void loadProfileInfo(){
        mEmailTextView.setText(user.getEmail());
        mNameTextView.setText(user.getDisplayName());

        if(user.getPhotoUrl()!=null)
            Glide.with(this).load(user.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_bee_hexagonal_logo)
                    .into(mAvatarImageView);
    }


    private void getGameStats(){

        // Cloud Firestore Instance
        if(dataBase==null)
            dataBase = FirebaseFirestore.getInstance();
        if(user==null)
            user = FirebaseAuth.getInstance().getCurrentUser();

        dataBase.collection(GAME_DATABASE)
                .whereEqualTo(GAME_USER_ID,user.getUid())
                .orderBy(GAME_TIMESTAMP, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                Game lastGame = task.getResult().getDocuments().get(0).toObject(Game.class);
                                loadStats(task.getResult().getDocuments());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void loadStats(List<DocumentSnapshot> documentSnapshot){

        mProfileStatsLoading.setVisibility(View.GONE);
        mProfileStatLinearLayout.setVisibility(View.VISIBLE);

        int nbOdGames = documentSnapshot.size();
        /*for (int i = 0; i < documentSnapshot.size() ; i++) {
            documentSnapshot.get(i).get(GA)
        }*/
        mTotalNumberGames.setText(String.valueOf(nbOdGames));

        loadCharts(documentSnapshot);

    }

    void loadCharts(List<DocumentSnapshot> documentSnapshot){

        List<Entry> entriesGamesXP = new ArrayList<Entry>();
        List<Entry> entriesGamesNbTotalWords = new ArrayList<Entry>();
        List<Entry> entriesGamesNbWronglWords = new ArrayList<Entry>();
        List<Entry> entriesGamesNbCorrectWords = new ArrayList<Entry>();
        List<PieEntry> pieChartDataEntry = new ArrayList<PieEntry>();


        for (int i =  0; i <documentSnapshot.size()  ; ++i) {
            Game tmp = documentSnapshot.get(i).toObject(Game.class);

            int totalWords = tmp.getPlayedWords().size(), correctWords= 0, wrongWords=0;
            for (int j = 0; j < totalWords; j++) {
                if(tmp.getPlayedWords().get(j).getResult())
                    correctWords++;
                else
                    wrongWords++;
            }

            mTotalGamesTotalNb +=totalWords;
            mTotalGamesTotalCorrectNb += correctWords;
            mTotalGamesTotalWrongNb += wrongWords;

            entriesGamesNbTotalWords.add(new Entry(i+1,totalWords));
            entriesGamesNbCorrectWords.add(new Entry(i+1,correctWords));
            entriesGamesNbWronglWords.add(new Entry(i+1,wrongWords));
            entriesGamesXP.add(new Entry(i+1,tmp.getTotalScore()));
        }

        /*
        * Pie chart
        */
        pieChartDataEntry.add(new PieEntry((float)mTotalGamesTotalCorrectNb/(float)mTotalGamesTotalNb * 100,"Correct Words"));
        pieChartDataEntry.add(new PieEntry((float)mTotalGamesTotalWrongNb/(float)mTotalGamesTotalNb * 100,"Wrong Words"));

        ArrayList<Integer> pieChartColors = new ArrayList<>();
        pieChartColors.add(Color.rgb(170,235,102));
        pieChartColors.add(Color.rgb(255,58,58));

        PieDataSet dataSet = new PieDataSet(pieChartDataEntry,"");
        dataSet.setColors(pieChartColors);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextSize(10f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
//        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(R.color.black);
//        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        pieChart.setTouchEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.animateXY(2500, 2500);

        // Set textViews
        mTotalNumberWordsTextView.setText(String.valueOf(mTotalGamesTotalNb));
        mTotalCorrectWordsTextView.setText(String.valueOf(mTotalGamesTotalCorrectNb));
        mTotalWrongWordsTextView.setText(String.valueOf(mTotalGamesTotalWrongNb));

        /*
        * Game Xp Linear Chart
        */

        // add entries to dataset
        LineDataSet dataSetGameXP = new LineDataSet(entriesGamesXP, "Game XP");

        // customize dataset
        dataSetGameXP.setLineWidth(3f);
        dataSetGameXP.setColor(R.color.colorSecondary);
        dataSetGameXP.setCircleColor(R.color.colorSecondaryDark);
        dataSetGameXP.setCircleRadius(5f);
        dataSetGameXP.setValueTextSize(10f);
        dataSetGameXP.setDrawValues(false);
        dataSetGameXP.setFillDrawable(ContextCompat.getDrawable(mContext,R.drawable.chart_fill));
        dataSetGameXP.setDrawFilled(true);
        dataSetGameXP.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        LineData lineData = new LineData(dataSetGameXP);


        mChartXP.getXAxis().setDrawLabels(false);
        mChartXP.getAxisRight().setDrawLabels(false);
//        mChartXP.getAxisLeft().setDrawGridLines(false);
        mChartXP.getAxisRight().setDrawGridLines(false);
        mChartXP.getDescription().setEnabled(false);
        mChartXP.getAxisLeft().setAxisMinimum(0f);

        mChartXP.getXAxis().setDrawGridLines(false);
//        mChartXP.getAxisLeft().setDrawLabels(false);
        mChartXP.setData(lineData);
        mChartXP.animateY(5000);
        mChartXP.animateX(5000);
        mChartXP.setTouchEnabled(false);

        mChartXP.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mChartXP.invalidate();


        /*
         * Number of words Linear Chart
         */

        LineDataSet dataSetTotalWords = new LineDataSet(entriesGamesNbTotalWords,"Total nb words");
        // customize dataset
        dataSetTotalWords.setLineWidth(3f);
        dataSetTotalWords.setColor(R.color.colorSecondary);
        dataSetTotalWords.setCircleColor(R.color.colorSecondary);
        dataSetTotalWords.setCircleRadius(2f);
//        dataSetGameXP.setValueTextSize(10f);
        dataSetTotalWords.setDrawCircles(false);
        dataSetTotalWords.setDrawValues(false);
        dataSetTotalWords.setFillDrawable(ContextCompat.getDrawable(mContext,R.drawable.chart_fill));
//        dataSetTotalWords.setFillColor(Color.GRAY);
        dataSetTotalWords.setDrawFilled(true);
        dataSetTotalWords.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        LineDataSet dataSetCorrectWords = new LineDataSet(entriesGamesNbCorrectWords,"Correct nb words");
        // customize dataset
        dataSetCorrectWords.setLineWidth(1f);
        dataSetCorrectWords.setColor(Color.GREEN);
        dataSetCorrectWords.setCircleColor(Color.GREEN);
        dataSetCorrectWords.setCircleRadius(2f);
        //        dataSetGameXP.setValueTextSize(10f);
        dataSetCorrectWords.setDrawValues(false);
        dataSetCorrectWords.setDrawCircles(false);
        //        dataSetGameXP.setFillDrawable(ContextCompat.getDrawable(mContext,R.drawable.chart_fill));
        dataSetCorrectWords.setFillColor(Color.GREEN);
        dataSetCorrectWords.setDrawFilled(true);
        dataSetCorrectWords.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        LineDataSet dataSetWrongWords = new LineDataSet(entriesGamesNbWronglWords,"Wrong nb words");
        // customize dataset
        dataSetWrongWords.setLineWidth(3f);
        dataSetWrongWords.setColor(Color.RED);
        dataSetWrongWords.setCircleColor(Color.RED);
        dataSetWrongWords.setCircleRadius(2f);
        dataSetWrongWords.setDrawCircles(false);
        //        dataSetGameXP.setValueTextSize(10f);
        dataSetWrongWords.setDrawValues(false);
        //        dataSetGameXP.setFillDrawable(ContextCompat.getDrawable(mContext,R.drawable.chart_fill));
        dataSetWrongWords.setFillColor(Color.RED);
        dataSetWrongWords.setDrawFilled(true);
        dataSetWrongWords.setMode(LineDataSet.Mode.CUBIC_BEZIER);


        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetTotalWords);
        dataSets.add(dataSetCorrectWords);
        dataSets.add(dataSetWrongWords);

        LineData lineData1 = new LineData(dataSets);


        mChartNbWords.getXAxis().setDrawLabels(false);
        mChartNbWords.getAxisRight().setDrawLabels(false);
        //        mChartXP.getAxisLeft().setDrawGridLines(false);
        mChartNbWords.getAxisRight().setDrawGridLines(false);
        mChartNbWords.getDescription().setEnabled(false);
        mChartNbWords.getAxisLeft().setAxisMinimum(0f);

        mChartNbWords.getXAxis().setDrawGridLines(false);
        //        mChartXP.getAxisLeft().setDrawLabels(false);
        mChartNbWords.setData(lineData1);
//        mChartNbWords.animateY(5000);
        mChartNbWords.animateX(500);
        mChartNbWords.setTouchEnabled(false);
        mChartNbWords.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mChartNbWords.invalidate();

    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).setActionBarTitle("Profile & Stats");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        loadProfileInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileInfo();
    }
}
