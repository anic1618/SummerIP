package com.example.iiitd.ip1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.iiitd.ip1.Utitlity.GoogleSignInUtility;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
    public static final String TAG = "MainActivity";
    private static final String EXTRA_IS_SIGN_IN="com.example.iiitd.ip1.is_sign_in";
    private GoogleSignInUtility mGoogleSignInUtility;
    private ProgressDialog mProgressDialog = null;
    private SignInButton mSignInButton = null;
    private boolean IsSignIn = true;
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
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        //
        IsSignIn= getIntent().getBooleanExtra(EXTRA_IS_SIGN_IN,false);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!IsSignIn){
            initView(false);

        }
        GoogleSignInResult googleSignInResult = mGoogleSignInUtility.checkUserCacheAvaliable();
        if (googleSignInResult != null) {
            handelSignInResult(googleSignInResult);
            //initView(true);
        }
        else initView(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initView(boolean isSignIn) {
        //
        if (!isSignIn) {
            mSignInButton.setSize(SignInButton.SIZE_STANDARD);
            mSignInButton.setScopes(mGoogleSignInUtility.getGoogleSignInOption().getScopeArray());
            updateUI(false,null,null);
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
        Log.d(TAG, "handleSignInResult:" + googleSignInResult.isSuccess());
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
        /*if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            Intent i = HomeActivity.newIntent(MainActivity.this,emailID,userName);

            //to put MainActivity on top of stack with no other activities of our
            // application on the backstack.

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

        } else {
            //  mStatusTextView.setText(R.string.signed_out);
            Log.d(TAG, "updateUI: logout pressed");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }*/
    }

    public static Intent newIntent(Context packageContext,Boolean signOut){
        Intent i = new Intent(packageContext,HomeActivity.class);
        i.putExtra(EXTRA_IS_SIGN_IN,signOut);
        return i;

    }

}
