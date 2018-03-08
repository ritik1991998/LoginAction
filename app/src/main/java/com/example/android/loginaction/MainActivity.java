package com.example.android.loginaction;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;


public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private static String mUserId;
    private static String mUser;
    private static Uri mUserProfile;
    private String mEmailId;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private LinearLayout mainLayout;
    private LoginActivity gg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mUserId = ANONYMOUS;
        mUserProfile = null;
        mEmailId = "";

        mFirebaseAuth = FirebaseAuth.getInstance();
        TextView userName = findViewById(R.id.userName);
        mainLayout=(LinearLayout)findViewById(R.id.main_content);

        ImageView profilePic = findViewById(R.id.profile_image);
        if (MainActivity.mUser != null) {
            userName.setText(mUser);
        } else {
            userName.setVisibility(View.GONE);
        }

        try {
            if (mUserProfile != null) {
                Log.i(mUserProfile.toString(), "standpoint pr60");
//            Glide.with(profilePic.getContext())
//                    .load(MainActivity.mUserProfile)
//                    .into(profilePic);

//                profilePic.setImageURI(MainActivity.mUserProfile);
                com.squareup.picasso.Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();
                Picasso.with(this)
                        .load(mUserProfile.toString())
                        .transform(transformation)
                        .into(profilePic);
            } else {
                Log.i("profile pic=null", "standpoint pr75");

                profilePic.setImageResource(R.drawable.icon_profile_empty);
            }
        } catch (Exception e) {
            profilePic.setImageResource(R.drawable.icon_profile_empty);
        }

        TextView emailId = findViewById(R.id.email);
        emailId.setText(email);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //to find if user is signed or not
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed
//                    Toast.makeText(MainActivity.this, "Welcome to FriendlyChat!", Toast.LENGTH_SHORT).show();
                    onSignInitilize(user.getUid(), user.getEmail(), user.getPhotoUrl());
                    mUser = user.getDisplayName();//send name and operate with db
                    Log.i(mUser, "point M99");

                } else {
                    //user signed out
                    onSignOutCleaner();
                    startActivityForResult((new Intent(getApplicationContext(), com.example.android.loginaction.LoginActivity.class)),
                            RC_SIGN_IN);
                    Snackbar snackbar = Snackbar.make(mainLayout, "Logged out successfully", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.GREEN);
                    snackbar.show();
                }
            }
        };
    }

    private void onSignInitilize(String userid, String email, Uri profilePic) {
        mUserId = userid;
        mEmailId = email;
        mUserProfile = profilePic;

    }

    private void onSignOutCleaner() {
        mUserId = ANONYMOUS;
        mEmailId = "";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//signing prosses result called before onResume
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) { //if returns request code sign in
            if (resultCode == RESULT_OK) {//successful login
                Snackbar snackbar = Snackbar.make(mainLayout, "Logged in successfully", Snackbar.LENGTH_SHORT);//snackbar
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();

            } else if (resultCode == RESULT_CANCELED) {//dont login
                Snackbar snackbar = Snackbar.make(mainLayout, "Logging in failed!!", Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        Log.i("onpause", "point m138");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("resume", "point m144");

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}