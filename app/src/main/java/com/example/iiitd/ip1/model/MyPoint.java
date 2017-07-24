package com.example.iiitd.ip1.model;

import java.sql.Timestamp;

/**
 * Created by iiitd on 21/7/17.
 */

public class MyPoint {
    public float readings;
    public Timestamp timestamp;

    public MyPoint(float readings, Timestamp timestamp) {
        this.readings = readings;
        this.timestamp = timestamp;
    }
    public MyPoint(){

    }


    public float getReadings() {

        return readings;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
