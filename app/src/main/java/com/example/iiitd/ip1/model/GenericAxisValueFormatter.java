package com.example.iiitd.ip1.model;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by iiitd on 20/7/17.
 */

public class GenericAxisValueFormatter implements IAxisValueFormatter {

    private String unit;
    public GenericAxisValueFormatter(String unit){
        this.unit = unit;
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf(value)+" "+unit;
    }
}
