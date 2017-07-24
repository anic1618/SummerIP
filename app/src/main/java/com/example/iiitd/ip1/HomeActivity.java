package com.example.iiitd.ip1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.iiitd.ip1.model.DownloadCallback;
import com.example.iiitd.ip1.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener,DownloadCallback{
    private static final String TAG = "HomeActivity";
    private static final String EXTRA_RESULT="com.example.iiitd.ip1.result";
    private static final String EXTRA_NET="com.example.iiitd.ip1.net";
   // private static final String EXTRA_USERNAME="com.example.iiitd.ip1.username";
    private String email;
    private Boolean exit = false;
    private GoogleApiClient mGoogleApiClient;
    private Button analyseBtn;
    private User user;
    private boolean mDownloading;
    private Gson gson;
    private RealTimeFragment fragment;
    private NetworkFragment mNetworkFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gson = new GsonBuilder().create();
        /*user = gson.fromJson(getIntent().getStringExtra(EXTRA_RESULT),User.class);
        */
        user = (User) getIntent().getSerializableExtra(EXTRA_RESULT);
       // user.setResult("{}");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                 .build();
        analyseBtn = (Button)findViewById(R.id.analyseBtn);
        analyseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = SummaryActivity.newIntent(HomeActivity.this);
                startActivity(i);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
       // bundle.put("edttext", "From Activity");
        fragment = (RealTimeFragment) fm.findFragmentById(R.id.home_chart_container1);
        if(fragment == null){
            //fragment = new PieChartFragment();
            fragment = RealTimeFragment.newInstance(user);
            fm.beginTransaction()
                    .add(R.id.home_chart_container1,fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigatonItemSelected: ");
        // Handle navigation view item clicks here.
       int id = item.getItemId();

        if (id == R.id.nav_edit_details) {
            // Handle the camera action
        } else if (id == R.id.nav_add_appliances) {

        } else if (id == R.id.nav_complain_FMS) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_sign_out) {
           Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent i = Main2Activity.newIntent(HomeActivity.this,false);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);finish();

                        }
                    });
            /*(new GoogleSignInUtility(this))
                    .registerConnectionCallBack(this,this)
                    .signOut();*/
           /* Intent i = Main2Activity.newIntent(HomeActivity.this,false);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);finish();*/

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Intent newIntent(Context packageContext,User user){
        Intent i = new Intent(packageContext,HomeActivity.class);
        i.putExtra(EXTRA_RESULT,user);
        //i.putExtra(EXTRA_NET,NetworkFragment);
        return i;

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }



    @Override
    public void updateFromDownload(String result) {
        Log.d(TAG, "updateFromDownload: ");
        Log.d(TAG, "updateFromDownload: got"+result);
        String jsonString = result;
        try {
            JSONObject json = new JSONObject(jsonString);
            String rs1 = json.getString("result");
            Log.d(TAG, "updateFromDownload: Json"+rs1);
            if(rs1.equals("bad")||rs1.equals("exception")){
                Log.d(TAG, "updateFromDownload: errr");
            }
            else {
                user = gson.fromJson(rs1,User.class);
                Log.d(TAG, "updateFromDownload: "+user.getData());
                fragment.updateFromDownloadV1(user);
            }
        } catch (Exception e) {
            Log.d(TAG, "updateFromDownload: "+e);
            e.printStackTrace();
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
