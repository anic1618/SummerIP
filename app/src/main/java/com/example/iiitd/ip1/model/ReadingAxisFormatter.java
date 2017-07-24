package com.example.iiitd.ip1.model;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by iiitd on 24/7/17.
 */

public class ReadingAxisFormatter implements IAxisValueFormatter {
    private String unit;

    public ReadingAxisFormatter(String unit){
        this.unit = "K"+" "+unit;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((value/1000))+unit;
    }
}
