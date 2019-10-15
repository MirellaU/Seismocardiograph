package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "PlotActivity";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    public ArrayList<Float> accValues = new ArrayList<Float>();
    IntentFilter accValuesIntentFilter;

    @BindView(R.id.accChartID)
    LineChart accChart;

    private BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                Log.d(TAG,"I'm in BR");
                accValues = (ArrayList<Float>) intent.getSerializableExtra("ACC_VALUES");
                Log.d(TAG,accValues.toString());
                addAccValuesEntry();
            }
        }
    };

    private void addAccValuesEntry() {
        LineData data = accChart.getData();

        if (data == null) {
            data = new LineData();
            accChart.setData(new LineData());
        }

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < accValues.size(); i++) {
            values.add(new Entry(i,(accValues.get(i))));
        }

        removeDataSet(accChart);

        LineDataSet set = new LineDataSet(values, "Acc Data");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);

        set.setColor(Color.BLUE);
        set.setCircleColor(Color.BLUE);
        set.setHighLightColor(Color.BLUE);
        set.setValueTextSize(0f);
        set.setDrawCircleHole(true);
        set.setCircleHoleColor(Color.BLUE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setValueTextColor(Color.RED);

        data.addDataSet(set);
        data.notifyDataChanged();
        accChart.notifyDataSetChanged();
        accChart.invalidate();
    }

    private void removeDataSet(LineChart chart) {
        LineData data = chart.getData();
        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        accValuesIntentFilter = new IntentFilter("NEW_ACC_VALUES");
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

        registerReceiver(accValuesReceiver, accValuesIntentFilter);
        Log.d(TAG,"Register the IF and BR");
    }

    @Override
    public void onPause () {
        super.onPause();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        unregisterReceiver(accValuesReceiver);
    }

}
