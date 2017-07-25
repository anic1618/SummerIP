package com.example.iiitd.ip1.Utitlity;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by iiitd on 23/6/17.
 */

public class MyTimeFormatter implements IAxisValueFormatter {

    protected String[] mtime = new String[]{"12 PM","1 AM","2 AM","3 AM","4 AM","5 AM","6 AM","7 AM","8 AM","9 AM","10 AM","11 AM","12 AM","1 PM","2 PM","3 PM","4 PM","5 PM","6 PM","7 PM","8 PM","9 PM","10 PM","11 PM","12 PM"};
       @Override
    public String getFormattedValue(float value, AxisBase axis) {
           return mtime[((int)value)%24];
    }
}
