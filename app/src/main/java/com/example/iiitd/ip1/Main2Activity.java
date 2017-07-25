package com.example.iiitd.ip1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.iiitd.ip1.model.DownloadCallback;
import com.example.iiitd.ip1.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class Main2Activity extends FragmentActivity /*AppCompatActivity*/ implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,DownloadCallback {

    public static final String TAG = "Main2Activity";
    private static final String EXTRA_IS_SIGN_IN="com.example.iiitd.ip1.is_sign_in";
    //private GoogleSignInUtility mGoogleSignInUtility;
    private ProgressDialog mProgressDialog ;
    private SignInButton mSignInButton ;
    private Button mSignOutButton ;
    private GoogleApiClient mGoogleApiClient;
    private boolean IsSignIn = true;
    private static final int RC_SIGN_IN = 9001;
    private String mURL ;
    private String mdata;
    private User user;
    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;
    private Gson gson;

    private void startDownload(User user) {
        Log.d(TAG, "startDownload: mDownloading=" + mDownloading);
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            Log.d(TAG, "starting Download: ");
            mNetworkFragment.startDownload(user);
            mDownloading = true;
        }
        else{
            Log.d(TAG, "Download: failed ");
        }
    }

    public String getURL(){
        return mURL;
    }

    public String getData(){
        return mdata;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gson = new GsonBuilder().create();
        user = new User();
        //user.setResult("{ url: "+ "\"http://192.168.33.40:8000/testing/verify\", data: \" " + +"\" }");
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
       // mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "http://192.168.33.40/testing/verify");
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        //mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        //mSignOutButton.setVisibility(View.VISIBLE);
        mSignInButton.setOnClickListener(this);
        //mSignOutButton.setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
        mGoogleApiClient.connect();

        mSignInButton.setSize(SignInButton.SIZE_STANDARD);

        IsSignIn= getIntent().getBooleanExtra(EXTRA_IS_SIGN_IN,true);

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        if(!IsSignIn) {mGoogleApiClient.connect(); return;}
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            showProgressDialog();

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        /*if (googleSignInResult != null) {
            handelSignInResult(googleSignInResult);
            //initView(true);
        }
        else initView(false);*/
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        hideProgressDialog();

    }


    public void initView(boolean isSignIn) {
        //
        if (!isSignIn) {
            //mSignInButton.setSize(SignInButton.SIZE_STANDARD);
           // mSignInButton.setScopes(mGoogleSignInUtility.getGoogleSignInOption().getScopeArray());
            updateUI(false,null);
        }

        //mSignInButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


        /*GoogleSignInResult googleSignInResult = mGoogleSignInUtility.detectResultReturn(requestCode, resultCode, data);
        if(googleSignInResult != null){
            handelSignInResult(googleSignInResult);
        }
        super.onActivityResult(requestCode, resultCode, data);*/
    }

    private void handleSignInResult(GoogleSignInResult googleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + googleSignInResult.isSuccess());

        if (googleSignInResult.isSuccess()) {
            Log.d(TAG, " login success : " + googleSignInResult.getSignInAccount().getEmail());
            Log.d(TAG, " id auth code : " + googleSignInResult.getSignInAccount().getServerAuthCode());
            Log.d(TAG, " login token : " + googleSignInResult.getSignInAccount().getIdToken());
            Log.d(TAG, " image : " + googleSignInResult.getSignInAccount().getPhotoUrl());
            GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
            String idToken = acct.getIdToken();
            Log.d(TAG, "handleSignInResult: "+idToken);
            //String jsonStr = "{ \"tokenId\" : \"" +  idToken + "\" }";
            mURL = "http://192.168.33.40:8000/testing/verify";
            mdata = idToken;
            user.setUrl(mURL);
            user.setData(mdata);
            startDownload(user);
            //sendTokenToServer(idToken);
            
            //updateUI(true,googleSignInResult.getSignInAccount().getEmail(),googleSignInResult.getSignInAccount().getDisplayName());
        } else {
            Log.d(TAG, " login failed");
            //updateUI(false,null,null);
        }
    }
    void sendTokenToServerV2(String idToken){

    }

    void sendTokenToServer(String idToken) {
        URL url = null;
        try {
            url = new URL("http://some-server");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Accept", "application/json");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }


        conn.setDoOutput(true);
        conn.setDoInput(true);
        //conn.addRequestProperty("idToken", idToken);

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            writer.write("{ \"tokenId\" : \"" +  idToken + "\" }");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        InputStream is = null;
        try {
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                is = conn.getInputStream();// is is inputstream
            } else {
                is = conn.getErrorStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //HttpClient httpClient = new DefaultHttpClient();
        //HttpPost httpPost = new HttpPost("https://yourbackend.example.com/tokensignin");

        try {
            //List nameValuePairs = new ArrayList(1);
            //nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
            BufferedReader br;

            InputStream in = new BufferedInputStream(conn.getInputStream());
            if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(in));
            } else {
                br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            }
            String output, sb = "";
            try {
                while ((output = br.readLine()) != null) {
                    sb.concat(output);
                }
                is.close();
                Log.d(TAG, "sendTokenToServer: "+ sb.toString());
                // httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //HttpResponse response = httpClient.execute(httpPost);
                int statusCode = conn.getResponseCode();
                //final String responseBody = EntityUtils.toString(response.getEntity());
                final String responseBody = sb;//conn.getResponseMessage() ;
                Log.i(TAG, "Signed in as: " + responseBody);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /*@Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }*/


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                Log.d(TAG, "onClick:sign_out_button ");
                signOut();
                break;
        }
    }

    private void updateUI(boolean signedIn,User user) {
        Log.d(TAG, "updateUI: ");
        if (signedIn) {
           // mSignInButton.setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
           // mSignOutButton.setVisibility(View.VISIBLE);


            Intent i = HomeActivity.newIntent(Main2Activity.this,user);

            //to put MainActivity on top of stack with no other activities of our
            // application on the backstack.

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

        } else {
            //  mStatusTextView.setText(R.string.signed_out);
            Log.d(TAG, "updateUI: logout pressed");
            mSignInButton.setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
           // mSignOutButton.setVisibility(View.GONE);
        }
    }

    public static Intent newIntent(Context packageContext, Boolean signOut){
        Intent i = new Intent(packageContext,Main2Activity.class);
        i.putExtra(EXTRA_IS_SIGN_IN,signOut);
        return i;

    }





    // [START signIn]
    private void signIn() {
        Log.d(TAG, "signIn: ");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Log.d(TAG, "signOut: ");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false,null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]


    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false,null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]


    @Override
    public void updateFromDownload(String result) {
        Log.d(TAG, "updateFromDownload: got"+result);
        String jsonString = result;
        try {
            JSONObject json = new JSONObject(jsonString);
            String rs1 = json.getString("result");
            if(rs1.equals("bad")||rs1.equals("exception")){
                updateUI(false,null);
            }
            else {
                user = gson.fromJson(rs1,User.class);
                Log.d(TAG, "updateFromDownload: "+user.getEmail());
                updateUI(true,user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }
}
