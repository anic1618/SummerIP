package com.example.iiitd.ip1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
    public static final String TAG = "MainActivity";
    private GoogleSignInUtility mGoogleSignInUtility;
    private SignInButton mSignInButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        //
        mGoogleSignInUtility = new GoogleSignInUtility(this);
        mGoogleSignInUtility.registerConnectionCallBack(this, this);
        //
        initView();
        //
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInResult googleSignInResult = mGoogleSignInUtility.checkUserCacheAvaliable();
        if (googleSignInResult != null) {
            handelSignInResult(googleSignInResult);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initView() {
        //mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        if (mSignInButton != null) {
            mSignInButton.setSize(SignInButton.SIZE_STANDARD);
            mSignInButton.setScopes(mGoogleSignInUtility.getGoogleSignInOption().getScopeArray());
        }

        //mSignInButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult googleSignInResult = mGoogleSignInUtility.detectResultReturn(requestCode, resultCode, data);
        if(googleSignInResult != null){
            handelSignInResult(googleSignInResult);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handelSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            Log.d(TAG, " login success : " + googleSignInResult.getSignInAccount().getEmail());
            Log.d(TAG, " id auth code : " + googleSignInResult.getSignInAccount().getServerAuthCode());
            Log.d(TAG, " login token : " + googleSignInResult.getSignInAccount().getIdToken());
            Log.d(TAG, " image : " + googleSignInResult.getSignInAccount().getPhotoUrl());
            updateUI(true,googleSignInResult.getSignInAccount().getEmail(),googleSignInResult.getSignInAccount().getDisplayName());
        } else {
            Log.d(TAG, " login failed");
            updateUI(false,null,null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, " onConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, " onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.sign_in_button:
                mGoogleSignInUtility.signIn();
                break;
            case R.id.sign_out_button:
                Log.d(TAG, "onClick:sign_out_button ");
                mGoogleSignInUtility.signOut(new ResultCallback<Status>(){
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false,null,null);
                        // [END_EXCLUDE]
                    }
                });
                break;
        }
    }

    private void updateUI(boolean signedIn,String emailID,String userName) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            Intent i = HomeActivity.newIntent(MainActivity.this,emailID,userName);
            startActivity(i);

        } else {
            //  mStatusTextView.setText(R.string.signed_out);
            Log.d(TAG, "updateUI: logout pressed");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

}
