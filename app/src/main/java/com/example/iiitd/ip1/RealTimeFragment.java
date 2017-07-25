package com.example.iiitd.ip1;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iiitd.ip1.Utitlity.MyTimeFormatter;
import com.example.iiitd.ip1.model.DownloadCallback;
import com.example.iiitd.ip1.model.MyPayLoad;
import com.example.iiitd.ip1.model.MyPoint;
import com.example.iiitd.ip1.model.ReadingAxisFormatter;
import com.example.iiitd.ip1.model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeFragment extends Fragment implements OnChartValueSelectedListener ,DownloadCallback {


    private LineChart mChart;
    private Typeface tf;
    private Typeface mTfRegular;
    private Typeface mTfLight;
    private static String TAG="RealTimeFragment";
    private static String[] chartName={"your usage","average usage"};
    private static String USER_KEY = "com.example.iiitd.ip1.RealTimeFragment";
    private NetworkFragment mNetworkFragment;
    private String mUrl="http://192.168.33.40:8000/api/readings/",mData="";
    private User user;
    private MyPayLoad myPayLoad;
    private Gson gson;
    private boolean mDownloading = false;
    public RealTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        user = (User) getArguments().getSerializable(USER_KEY);
        mNetworkFragment = NetworkFragment.getInstance(getActivity().getSupportFragmentManager());
       // myPayLoad = new MyPayLoad();
        user.setUrl(mUrl);
        gson = new GsonBuilder().create();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_real_time, container, false);
        mChart = (LineChart) v.findViewById(R.id.real_line_chart1);

        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        mChart.getDescription().setEnabled(false);

        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        mChart.setBackgroundColor(Color.GRAY);

        LineData data = new LineData();
        Log.d(TAG, "onCreateView: " + data.getXMax()+" "+data.getEntryCount()+" "+data.getXMin());
        data.setValueTextColor(Color.GRAY);
        //Log.d(TAG, "onCreateView: "+data.getDataSetLabels());
        // add empty data
        mChart.setData(data);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setXOffset(1);
        xl.setValueFormatter(new MyTimeFormatter() );

        //xl.setAxisMinimum(0);
       // xl.setAxisMaximum(24);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(new ReadingAxisFormatter("KW"));
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(60000f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);


        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        feedMultiple2();
    }

    private void addEntry() {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be calle`d as well

            if (set == null) {
                set = createSet(0);
                data.addDataSet(set);
            }

            if(set.getEntryCount()<48) {
                while(set.getEntryCount()!=48)
                    data.addEntry(new Entry((set.getEntryCount()), (float) (Math.random() * 40) + 30f), 0);
            }
            else{
                //data.getDataSets().get(0).removeEntry(0);
                data.addEntry(new Entry((set.getEntryCount()), (float) (Math.random() * 40) + 30f), 0);
            }
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(24);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX((data.getEntryCount()));

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private void  addEntry2R(List<MyPoint> myPoints) {


        //user.setResult();

        LineData data = mChart.getData();

        if (data != null) {

            LineDataSet set1 = (LineDataSet)data.getDataSetByIndex(0);
            //LineDataSet set2 = (LineDataSet)data.getDataSetByIndex(1);
            // set.addEntry(...); // can be calle`d as well

            if (set1 == null) {
                set1 = createSet(0);
                //set2 = createSet(1);
                data.addDataSet(set1);
                //data.addDataSet(set2);

            }

            int i=0;
            if(set1.getEntryCount()<20) {
                while(set1.getEntryCount()!=12){
                    i++;
                    data.addEntry(new Entry(i ,  myPoints.get(i).getReadings()), 0);

                }

               /* while(set2.getEntryCount()!=9)
                    data.addEntry(new Entry((set2.getEntryCount()), (float) (Math.random() * 40) + 30f), 1);*/
            }
            else{
                //data.getDataSets().get(0).removeEntry(0);
                //data.addEntry(new Entry((set1.getEntryCount()), (float) (Math.random() * 40) + 30f), 0);
                //data.addEntry(new Entry((set2.getEntryCount()), (float) (Math.random() * 40) + 30f), 1);
            }
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(6);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX((data.getEntryCount()));

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private void  addEntry2() {


        //user.setResult();

        LineData data = mChart.getData();

        if (data != null) {

            LineDataSet set1 = (LineDataSet)data.getDataSetByIndex(0);
            LineDataSet set2 = (LineDataSet)data.getDataSetByIndex(1);
            // set.addEntry(...); // can be calle`d as well

            if (set1 == null) {
                set1 = createSet(0);
                //set2 = createSet(1);
                data.addDataSet(set1);
                //data.addDataSet(set2);

            }


            if(set1.getEntryCount()<48) {
                /*while(set1.getEntryCount()!=48)
                    data.addEntry(new Entry((set1.getEntryCount()), ), 0);*/
                /*while(set2.getEntryCount()!=48)
                    data.addEntry(new Entry((set2.getEntryCount()), (float) (Math.random() * 40) + 30f), 1);*/
            }
            else{
                //data.getDataSets().get(0).removeEntry(0);
                data.addEntry(new Entry((set1.getEntryCount()), (float) (Math.random() * 40) + 30f), 0);
                data.addEntry(new Entry((set2.getEntryCount()), (float) (Math.random() * 40) + 30f), 1);
            }
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(24);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX((data.getEntryCount()));

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet(int i) {

        LineDataSet set = new LineDataSet(null,chartName[i]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(mColors[i]);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);

        set.setFillAlpha(65);

        if(i==0){
            set.setLineWidth(2f);
            set.setCircleRadius(4f);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
        }
        else{
            set.enableDashedLine(5,10,0);
            set.setLineWidth(2.5f);
            set.setFillColor(mColors[i]);
            set.setHighLightColor(mColors[i]);
        }
        return set;
    }

    private Thread thread;

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry2();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(25000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private void feedMultiple2() {
        Log.d(TAG, "feedMultiple2: "+user.getUrl());
       // mNetworkFragment.startDownload(user);
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                //mNetworkFragment.startDownload(user);
                addEntry2();
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(runnable);
            }
        }, 0, 5000);//1000*60*60
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    private int[] mColors = new int[] {
            Color.rgb(137, 230, 81),
            Color.rgb(240, 240, 30),
            Color.rgb(89, 199, 250),
            Color.rgb(250, 104, 104)
    };

    private void setupChart(LineChart chart, LineData data, int color) {

        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(color);

        // no description text
        chart.getDescription().setEnabled(false);

        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10, 0, 10, 0);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setEnabled(false);

        // animate calls invalidate()...
        chart.animateX(2500);
    }

    private LineData getData(int count, float range) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
            yVals.add(new Entry(i, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(1.75f);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(2.5f);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.WHITE);
        set1.setDrawValues(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);

        return data;
    }


    public void updateFromDownloadV1(User user) {
       // Log.d(TAG, "updateFromDownload: "+result);
       /* JSONObject json = null;
        String rs1 = null;
        try {
            json = new JSONObject(result);
            rs1 = json.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user = gson.fromJson(rs1,User.class);
*/
        @SuppressWarnings("serial")
        Type collectionType = new TypeToken<List<MyPoint>>() {
        }.getType();
        List<MyPoint> myPoints = gson.fromJson(user.getData(),collectionType);
        for(MyPoint point :myPoints){
            Log.d(TAG, "updateFromDownload: "+point.getReadings()+" "+point.timestamp);
        }
        addEntry2R(myPoints);
    }



    @Override
    public void updateFromDownload(String result) {
        Log.d(TAG, "updateFromDownload: "+result);
       /* JSONObject json = null;
        String rs1 = null;
        try {
            json = new JSONObject(result);
            rs1 = json.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user = gson.fromJson(rs1,User.class);
*/
       @SuppressWarnings("serial")
        Type collectionType = new TypeToken<List<MyPoint>>() {
        }.getType();
        List<MyPoint> myPoints = gson.fromJson(user.getData(),collectionType);
        for(MyPoint point :myPoints){
            Log.d(TAG, "updateFromDownload: "+point.getReadings()+" "+point.timestamp);
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public static RealTimeFragment newInstance(User user){
        RealTimeFragment realTimeFragment = new RealTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        realTimeFragment.setArguments(bundle);

        return realTimeFragment;
    }
}
