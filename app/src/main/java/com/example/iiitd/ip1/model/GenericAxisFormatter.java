package com.example.iiitd.ip1.model;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by iiitd on 20/7/17.
 */

public class GenericAxisFormatter implements IAxisValueFormatter {
    protected String[] values;
    private BarLineChartBase<?> chart;

    public GenericAxisFormatter(BarLineChartBase chart,String[] values) {
        this.chart = chart;
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int) value];
    }
}