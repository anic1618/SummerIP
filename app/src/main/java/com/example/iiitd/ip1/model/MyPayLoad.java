package com.example.iiitd.ip1.model;

import java.io.Serializable;

/**
 * Created by iiitd on 23/7/17.
 */

public class MyPayLoad implements Serializable{
    private String mUrl;
    private String mData;

    public String getmData() {
        return mData;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }



    public String getmUrl() {
        return mUrl;
    }

    public MyPayLoad(){

    }

    public MyPayLoad(String mUrl, String mData) {
        this.mUrl = mUrl;
        this.mData = mData;
    }

    /*public String toString() {
        return "{\"url:\"" + "\"" + mUrl+"\"," +
                 "\"data:\""+"\"" + mData+"\"" +
                "}";
    }*/
}
