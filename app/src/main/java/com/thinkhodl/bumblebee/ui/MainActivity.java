package com.thinkhodl.bumblebee.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thinkhodl.bumblebee.BuildConfig;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.ui.fragments.LevelsFragment;
import com.thinkhodl.bumblebee.ui.fragments.ProfileFragment;
import com.thinkhodl.bumblebee.ui.fragments.RankingFragment;
import com.thinkhodl.bumblebee.ui.fragments.StatsFragment;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

//    @BindView(R.id.speed_game_button)
//    Button mSpeedGameButton;

    ImageView mUserAvatarImageView;

    TextView mUserEmailTextView;

    TextView mUserNameTextView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mContext = this;

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setNavHeaderProfileInfo();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mUserAvatarImageView = mNavigationView.getHeaderView(0).findViewById(R.id.user_avatar_imageView);
        mUserNameTextView = mNavigationView.getHeaderView(0).findViewById(R.id.user_name_textView);
        mUserEmailTextView = mNavigationView.getHeaderView(0).findViewById(R.id.user_email_textView);

        // check if user is logged in
        checkUserLogedIn();

        MenuItem item = mNavigationView.getMenu().getItem(0);
//        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_stats));
        onNavigationItemSelected(item);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fragmentManager = getSupportFragmentManager();


        switch (item.getItemId()){
            case R.id.nav_speed_game:
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new LevelsFragment()).commit();

                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();
                return true;
            case R.id.nav_rankings:
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new RankingFragment()).commit();

                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();
                return true;
            case R.id.nav_stats:
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new StatsFragment()).commit();

                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();
                return true;
            case R.id.nav_share:
                Intent shareLinkIntent = new Intent(Intent.ACTION_SEND);
                shareLinkIntent.setType("text/plain");
                shareLinkIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "Take a look at this amazing game");
                shareLinkIntent.putExtra(Intent.EXTRA_TEXT,
                        "Take a look at this amazing game!\n\n" +
                                "https://play.google.com/store/apps/details?id=com.thinkhodl.bumblebee");
                startActivity(Intent.createChooser(shareLinkIntent, "Share the Game"));
                break;
            case R.id.nav_send:
//                Intent intent = new Intent(this , ResultsActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.setData(Uri.parse("mailto:contact@thinkhodl.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback BumbleBee app");
                startActivity(Intent.createChooser(intent, "Contact"));
                break;
            case R.id.nav_profile:
                // Insert the fragment by replacing any existing fragment
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();

                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                return true;
            case R.id.nav_logout:
                signUserOut();
                break;

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setNavHeaderProfileInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        String userName = user.getDisplayName();
        Uri userAvatarURL = user.getPhotoUrl();
        if(userEmail!=null)
            mUserEmailTextView.setText(userEmail);
        else
            mUserEmailTextView.setVisibility(View.INVISIBLE);
        mUserNameTextView.setText(userName);

        if(userAvatarURL!= null)
            Glide.with(mContext)
                    .load(userAvatarURL)
                    .circleCrop()
                    .placeholder(R.drawable.ic_bee_hexagonal_logo)
                    .into(mUserAvatarImageView);
    }

    private void signUserOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(mContext,AuthActivity.class));
                            finish();
                        } else {
                            Toast.makeText(mContext,"Error during sign out",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserLogedIn(){

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) {
            startActivity(new Intent(mContext,AuthActivity.class));
            finish();
        }
        else
            setNavHeaderProfileInfo();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        MenuItem item = mNavigationView.getMenu().getItem(0);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else if(!item.isChecked()){
            onNavigationItemSelected(item);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        firebaseAuth.addAuthStateListener(mAuthListener);
    }




    @Override
    protected void onStop() {
        super.onStop();
//        firebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


}
