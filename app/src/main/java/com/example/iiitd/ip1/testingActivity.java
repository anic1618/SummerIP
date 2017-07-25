package com.example.iiitd.ip1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class testingActivity extends AppCompatActivity {
    //private ProgressBar progress;
    private TextView text;
     private final String TAG = "testingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (TextView) findViewById(R.id.testv11);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        (new DownloadWebPageTask()).execute("sdfsdf");
    }


    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            InputStream stream = null;
            HttpURLConnection connection = null;
            String result = null;

            Log.d(TAG, "downloadUrl: 1st try");
            try {
                connection = (HttpURLConnection) new URL("http://192.168.33.40:8000/testing/verify").openConnection();
                //if you are writing data then RequestMethod are post by default
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
               // connection.addRequestProperty("Accept", "application/json");
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                Log.d(TAG, "aaa downloadUrl: try");

               BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
                /*String str = URLEncoder.encode("tokenId", "UTF-8")
                        + "=" + URLEncoder.encode("idToken", "UTF-8");*/
                String str = "{\"tokenId\" : \"idToken\"}";

                Log.d(TAG, "aaa doInBackground: ");
                writer.write(str);
                writer.flush();
                writer.close();
                /*OutputStream os = connection.getOutputStream();
                byte[] outputBytes = "{ \"tokenId\" : \" idToken \" }".getBytes("UTF-8");
                Log.d(TAG, "doInBackground: "+ Arrays.toString(outputBytes));
                os.write(outputBytes);
                os.flush();
                os.close();*/

                connection.connect();
                int responseCode = 0;
                responseCode = connection.getResponseCode();

                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                stream = connection.getInputStream();
                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 500);
                }
                return result;
            }catch ( IOException e1) {
                Log.d(TAG, "doInBackground: "+e1);
                e1.printStackTrace();
            } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.d(TAG, "doInBackground: "+e);
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
            Log.d(TAG, "downloadUrl: end");
            return result;


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
                //publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS);
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
    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: "+result);
        text.setText(result);
    }
}



}
