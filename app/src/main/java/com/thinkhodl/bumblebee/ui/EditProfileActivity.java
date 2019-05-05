package com.thinkhodl.bumblebee.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thinkhodl.bumblebee.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.change_profile_picture_button)
    MaterialButton mChangePictureButton;

    @BindView(R.id.avatar_profile_imageView)
    ImageView mAvatarImageView;

    @BindView(R.id.name_profile_editText)
    TextInputEditText mNameEditText;

    /*
    @BindView(R.id.username_profile_editText)
    TextInputEditText mUsernameEditText;
    */

    @BindView(R.id.email_profile_editText)
    TextInputEditText mEmailEditText;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser;

    // Avatar uri
    private Uri mAvatarUri;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);
        mContext = this;

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Edit Profile");

        mAvatarImageView.setClipToOutline(true);

        loadUserInfo();


        mChangePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(100, 100)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setFixAspectRatio(true)
                        .start((Activity) mContext);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mAvatarUri = resultUri;
                mAvatarImageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void loadUserInfo() {
        // Get user from firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        if (mUser.getDisplayName() != null)
            mNameEditText.setText(mUser.getDisplayName());
        if (mUser.getEmail() != null)
            mEmailEditText.setText(mUser.getEmail());
        if (mUser.getPhotoUrl() != null)
            Glide.with(mContext)
                    .load(mUser.getPhotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_bee_hexagonal_logo)

                    .into(mAvatarImageView);
    }

    private void updateUser() {

        UserProfileChangeRequest profileUpdates = null;

        if (mAvatarUri != null && TextUtils.isEmpty(mNameEditText.getText()))
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(mAvatarUri)
                    .build();
        else if (mAvatarUri != null && !TextUtils.isEmpty(mNameEditText.getText()))
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(mAvatarUri)
                    .setDisplayName(mNameEditText.getText().toString())
                    .build();

        if (TextUtils.isEmpty(mEmailEditText.getText()))
            mEmailEditText.setError("This field is mandatory");
        else if (!mEmailEditText.getText().equals(mUser.getEmail()))
            mUser.updateEmail(mEmailEditText.getText().toString());

        /*
        if(TextUtils.isEmpty(mUsernameEditText.getText()))
            mUsernameEditText.setError("This field is mandatory");

        */
        if (profileUpdates != null)
            mUser.updateProfile(profileUpdates);

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    void confirmCancel() {
        new AlertDialog.Builder(this, R.style.CustomDialogue)
                //                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cancel edit")
                .setMessage("Are you sure you want to leave the profile edit? \n Unsaved changes will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                updateUser();
                finish();
                break;
            case android.R.id.home:
                confirmCancel();
                //                NavUtils.navigateUpFromSameTask(this);
                break;
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        confirmCancel();
    }
}
