package com.thinkhodl.bumblebee.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thinkhodl.bumblebee.R;
import com.thinkhodl.bumblebee.ui.fragments.LevelsFragment;
import com.thinkhodl.bumblebee.ui.fragments.ProfileFragment;
import com.thinkhodl.bumblebee.ui.fragments.RankingFragment;
import com.thinkhodl.bumblebee.ui.fragments.StatsFragment;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

//    @BindView(R.id.speed_game_button)
//    Button mSpeedGameButton;

    ImageView mUserAvatarImageView;

    TextView mUserEmailTextView;

    TextView mUserNameTextView;

    DrawerLayout mDrawerLayout;

    NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mUserAvatarImageView = mNavigationView.getHeaderView(0).findViewById(R.id.user_avatar_imageView);
        mUserNameTextView = mNavigationView.getHeaderView(0).findViewById(R.id.user_name_textView);
        mUserEmailTextView = mNavigationView.getHeaderView(0).findViewById(R.id.user_email_textView);
        // check if user is logged in
        checkUserLogedIn();

        MenuItem item = mNavigationView.getMenu().getItem(0);
        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_profile));
//        onNavigationItemSelected(item);
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
            case R.id.nav_share: break;
            case R.id.nav_send:
                Intent intent = new Intent(this , ResultsActivity.class);
                startActivity(intent);
                return true;
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
                FirebaseAuth.getInstance().signOut();
                finish();
                break;

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                setNavHeaderProfileInfo();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
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
            Glide.with(this)
                    .load(userAvatarURL)
                    .circleCrop()
                    .placeholder(R.drawable.ic_honey)
                    .into(mUserAvatarImageView);
    }

    private void checkUserLogedIn(){

        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());
                    //                new AuthUI.IdpConfig.FacebookBuilder().build();


                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
                else
                    setNavHeaderProfileInfo();
            }


        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }




    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


}
