package com.example.iiitd.ip1;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.iiitd.ip1.model.DownloadCallback;
import com.example.iiitd.ip1.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
/**
 * This fragment return json(user or error message) received from server
 */
public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";
    public static final String inTAG = "DownloadTask ";
    private static final String URL_KEY = "UrlKey";
    private static final String USER_KEY = "com.example.iiitd.p1.NetworkFragment";
    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;
    private Gson gson;
    private String mUrlString;
    private String mData;
    private User user;
    public NetworkFragment() {
        // Required empty public constructor
    }


    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkFragment getInstance(FragmentManager fragmentManager) {
        /*Log.d(TAG, "getInstance: " + url + " "+data);
        NetworkFragment networkFragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        args.putString("DATA",data);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;*/



        NetworkFragment networkFragment = (NetworkFragment) fragmentManager
                .findFragmentByTag(NetworkFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new NetworkFragment();
            /*Bundle bundle = new Bundle();
            bundle.putSerializable(USER_KEY, user);
            networkFragment.setArguments(bundle);*/
            /*Bundle args = new Bundle();
            args.putString(URL_KEY, url);
            args.putString("DATA",data);
            networkFragment.setArguments(args);*/
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
       // user = (User) getArguments().getSerializable(USER_KEY);
        gson = new GsonBuilder().create();
        // mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        /* mUrlString = getArguments().getString(URL_KEY);
        mData = getArguments().getString("DATA");
        Log.d(TAG, "onCreate: " + mUrlString + " " + mData);*/
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: " + context.toString());
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload(User user) {
        Log.d(TAG, "startDownload: "+user.getUrl());
        this.user = user;
        cancelDownload();
        mDownloadTask = new DownloadTask(mCallback);
        mDownloadTask.execute (user.getUrl(),gson.toJson(user,User.class));
        /*if(mDownloadTask.isCancelled()) {
            Log.d(TAG, "startDownload: inside");*/
        //mDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user.getUrl(), gson.toJson(user, User.class));
        
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, DownloadTask.Result> {
        private DownloadCallback<String> mCallback;

        private DownloadTask(){

        }

        public DownloadTask(DownloadCallback<String> callback) {
            setCallback(callback);
        }

       /* public DownloadTask() {

        }*/

        void setCallback(DownloadCallback<String> callback) {
            mCallback = callback;
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        class Result {
            public String mResultValue;
            public Exception mException;
            public Result(String resultValue) {
                mResultValue = resultValue;
            }
            public Result(Exception exception) {
                mException = exception;
            }
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI  && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    Log.d(TAG, "onPreExecute: errrererer");




                    mCallback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected DownloadTask.Result doInBackground(String... urls) {
            Result result = null;
            Log.d(inTAG, "doInBackground: ");
            if (!isCancelled() && urls != null && urls.length > 0) {


                String urlString = urls[0];
                String data = null;

                if(urls.length > 1) data = urls[1];
                try {
                    Log.d(inTAG, "doInBackground:inside if "+urlString+" "+ data);
                   // URL url = new URL(urlString);
                    //String resultString = downloadUrl(url,data);
                    ////////////////////////////////////////////////////////////
                    Log.d(inTAG, "downloadUrl: ");
                    InputStream stream = null;
                    HttpURLConnection connection = null;
                    String resultString = null;
                    try {

                        //connection = (HttpsURLConnection) url.openConnection();

                        connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                        //connection = (HttpsURLConnection) new URL("https://google.com").openConnection();
                        Log.d(inTAG, "downloadUrl: 1st try");
                        // Timeout for reading InputStream arbitrarily set to 3000ms.
                        //connection.setReadTimeout(3000);
                        // Timeout for connection.connect() arbitrarily set to 3000ms.
                        //connection.setConnectTimeout(3000);
                        // For this use case, set HTTP method to GET.
                        connection.setRequestMethod("POST");
                        // Already true by default but setting just in case; needs to be true since this request
                        // is carrying an input (response) body.
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        Log.d(inTAG, "downloadUrl: try");
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
                        writer.write(data);
                        writer.flush();
                        writer.close();


                        // Open communications link (network traffic occurs here).
                        connection.connect();
                        publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                        int responseCode = connection.getResponseCode();
                        if (responseCode != HttpsURLConnection.HTTP_OK) {
                            throw new IOException("HTTP error code: " + responseCode);
                        }
                        // Retrieve the response body as an InputStream.
                        stream = connection.getInputStream();
                        BufferedReader Bin = new BufferedReader(new InputStreamReader(stream));
                        publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS);
                        String inputLine;
                        resultString = "";
                        while ((inputLine = Bin.readLine()) != null){
                            resultString += inputLine;
                        }
                        /*if (stream != null) {
                            // Converts Stream to String with max length of 500.
                            resultString = readStream(stream, 500);
                        } */
                    } catch(IOException e){
                        Log.d(TAG, "downloadUrl: errr"+e);
                    }
                    finally {
                        // Close Stream and disconnect HTTPS connection.
                        if (stream != null) {
                            stream.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                    Log.d(inTAG, "downloadUrl: end");

                    ///////////////////////////////////////////////////////
                    Log.d(inTAG, "doInBackground: got"+resultString);
                    if (resultString != null) {
                        result = new Result(resultString);
                    } else {
                        throw new IOException("No response received.");
                    }
                } catch(Exception e) {
                    Log.d(TAG, "doInBackground: wwwwww"+e);
                    result = new Result("{ \"result\" :\"exception\"}");
                }
            }
            else{
                Log.d(inTAG, "doInBackground: unable ");
            }
            return result;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Result result) {
            Log.d(TAG, "onPostExecute: "+ (result != null )+ (mCallback != null) );
            if (result != null && mCallback != null) {
                if (result.mException != null) {
                    mCallback.updateFromDownload(result.mException.getMessage());
                } else if (result.mResultValue != null) {
                    Log.d(TAG, "onPostExecute: "+result.mResultValue);
                    mCallback.updateFromDownload(result.mResultValue);
                }
                mCallback.finishDownloading();
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(Result result) {
        }
        private String downloadUrl(URL url , String data) throws IOException {
            Log.d(inTAG, "downloadUrl: ");
            InputStream stream = null;
            HttpURLConnection connection = null;
            String result = null;
            try {

                //connection = (HttpsURLConnection) url.openConnection();

                connection = (HttpURLConnection) new URL("http://192.168.33.40:8000/testing/verify").openConnection();
                //connection = (HttpsURLConnection) new URL("https://google.com").openConnection();
                Log.d(inTAG, "downloadUrl: 1st try");
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod("POST");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Log.d(inTAG, "downloadUrl: try");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();


                // Open communications link (network traffic occurs here).
                connection.connect();
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS);
                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 500);
                }
            } catch(IOException e){
                Log.d(TAG, "downloadUrl: errr");
            }
            finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.d(inTAG, "downloadUrl: end");
            return result;
        }

        private void publishProgress(int processInputStreamInProgress) {
        }
        private String readStream(InputStream stream, int maxLength) throws IOException {
            String result = null;
            // Read InputStream using the UTF-8 charset.
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            // Create temporary buffer to hold Stream data with specified max length.
            char[] buffer = new char[maxLength];
            // Populate temporary buffer with Stream data.
            int numChars = 0;
            int readSize = 0;
            while (numChars < maxLength && readSize != -1) {
                numChars += readSize;
                int pct = (100 * numChars) / maxLength;
                publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS);
                readSize = reader.read(buffer, numChars, buffer.length - numChars);
            }
            if (numChars != -1) {
                // The stream was not empty.
                // Create String that is actual length of response body if actual length was less than
                // max length.
                numChars = Math.min(numChars, maxLength);
                result = new String(buffer, 0, numChars);
            }
            return result;
        }
    }

}
