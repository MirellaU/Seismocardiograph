package com.example.mirella.seismocardiograph;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "ChartActivity";
    private SensorManager sensorManager;
    private Sensor sensor;

    @BindView(R.id.accChartID)
    LineChart accChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        //chart options
        accChart.setKeepPositionOnRotation(true);
        accChart.getDescription().setEnabled(true);
        accChart.getDescription().setText("");

        LineData data = new LineData();
        accChart.setData(data);

        YAxis leftAxis = accChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(0f); // start at 0
        //leftAxis.setAxisMaximum(100f); // the axis maximum is 100

        //YAxis rightAxis = accChart.getAxisRight();
        //rightAxis.setDrawGridLines(false); // no grid lines
        //rightAxis.setAxisMinimum(0f); // start at 0
        //rightAxis.setAxisMaximum(100f); // the axis maximum is 100

        XAxis xAxis = accChart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        accChart.getXAxis().setDrawLabels(false);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        };
        xAxis.setValueFormatter(xAxisFormatter);
    }

}
