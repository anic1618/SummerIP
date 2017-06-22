package com.example.iiitd.ip1;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

/**
 * Created by iiitd on 21/6/17.
 */

public class GoogleSignInUtility {

    private static final int RC_SIGN_IN = 9001;
    //
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    //
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private FragmentActivity mFragmentActivity;

    public GoogleSignInUtility(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    public void registerConnectionCallBack(GoogleApiClient.ConnectionCallbacks connectionCallbacks, GoogleApiClient
            .OnConnectionFailedListener connectionFailedListener) {
        this.mConnectionCallbacks = connectionCallbacks;
        this.mConnectionFailedListener = connectionFailedListener;
    }

    public GoogleApiClient getGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mFragmentActivity)
                    .enableAutoManage(mFragmentActivity, mConnectionFailedListener)
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOption())
                    .build();
        }
        return mGoogleApiClient;
    }

    public GoogleSignInOptions getGoogleSignInOption() {
        if (mGoogleSignInOptions == null) {
            mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    //.requestIdToken("serverIdToken")
                    //.setHostedDomain("hostDomain")
                    //.requestServerAuthCode("serverIdToken",false)
                    //.setAccountName("")
                    //.setHostedDomain()
                    .build();
        }
        return mGoogleSignInOptions;
    }
    public GoogleSignInResult checkUserCacheAvaliable(){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn
                (getGoogleApiClient());
        final GoogleSignInResult[] googleSignInRLT = {null};
        if(optionalPendingResult.isDone()){
            googleSignInRLT[0] = optionalPendingResult.get();
        }else{
            /*showProgressDialog();*/
            /*optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {*/
            /*    @Override*/
            /*    public void onResult(GoogleSignInResult googleSignInResult) {*/
            /*        hideProgressDialog();*/
            /*        handleSignInResult(googleSignInResult);*/
            /*    }*/
            /*});*/

        }
        return googleSignInRLT[0];
    }

    /**
     * get GoogleSignInResult from activityIntentResult
     * @param requestCode request code
     * @param resultCode result code
     * @param data data intent
     * @return googleSignInResult or null
     */
    public GoogleSignInResult detectResultReturn(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            return googleSignInResult;
        }
        return null;
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mFragmentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut(ResultCallback resultCallback) {
        Auth.GoogleSignInApi.signOut(getGoogleApiClient()).setResultCallback(resultCallback);
    }

    public void revokeAccess(ResultCallback resultCallback) {
        Auth.GoogleSignInApi.revokeAccess(getGoogleApiClient()).setResultCallback(resultCallback);
    }

}
