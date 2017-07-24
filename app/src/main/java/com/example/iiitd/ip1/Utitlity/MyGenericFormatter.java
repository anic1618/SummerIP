package com.example.iiitd.ip1.Utitlity;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by iiitd on 27/6/17.
 */

public class MyGenericFormatter implements IAxisValueFormatter {
    private String[] val;

    public void setVal(String[] val) {
        this.val = val;
    }

    public String getFormattedValue(float value, AxisBase axis) {
        return val[(int)value];
    }
}
