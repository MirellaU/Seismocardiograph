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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "PlotActivity";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    public float accVal;
    public int i = 0;
    ArrayList<Entry> values = new ArrayList<>();
    IntentFilter accValuesIntentFilter;

    @BindView(R.id.accChartID)
    LineChart accChart;

    private BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                //Log.d(TAG,"I'm in BR");
                //accVal = (ArrayList<Float>) intent.getSerializableExtra("ACC_VALUES");
                accVal = intent.getFloatExtra("ACC_VALUES",accVal);
//                if(accValues.size()!=0) {
//                    accPlotValues.add(accValues);
//                }
                Log.d(TAG, String.valueOf(accVal));
                addAccValuesEntry();
//                accPlotValues.add(accVal);
//                if(accPlotValues.size()>11) {
//                    addAccValuesEntry();
//                    accPlotValues.remove();
//                }
            }
        }
    };

    private void addAccValuesEntry() {
        LineData data = accChart.getData();

        if (data == null) {
            data = new LineData();
            accChart.setData(new LineData());
        }

        values.add(new Entry(i,accVal));

//        for(int j=i;j<20;j++){
//            plottedValues.add(values.get(i-20));
//        }

        LineDataSet set = new LineDataSet(values, null);

        set.setLineWidth(2.5f);
        set.setDrawCircles(false);
        //set.setCircleRadius(0f);
        set.setColor(R.color.colorPrimary);
        //set.setCircleColor(Color.BLUE);
        set.setHighLightColor(R.color.colorPrimary);
        set.setValueTextSize(0f);
        //set.setDrawCircleHole(false);
        //set.setCircleHoleColor(Color.BLUE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setValueTextColor(Color.RED);

        data.addDataSet(set);

        data.notifyDataChanged();
        accChart.notifyDataSetChanged();

        accChart.setVisibleXRangeMaximum(20);
        accChart.moveViewToX(i);
        //accChart.invalidate();
        //accChart.animateX(2000);

//        if(values.size()>20) {
//            values.remove(1);
//            values.clear();
//            i=-1;
//            //removeDataSet(accChart);
//        }
        i++;
    }

    private void removeDataSet(LineChart chart) {
        LineData data = chart.getData();
        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount()-1));
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
        accChart.getAxisRight().setDrawLabels(false);
        accChart.getLegend().setEnabled(false);

        final LineData data = new LineData();
        accChart.setData(data);

        YAxis leftAxis = accChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(0f); // start at 0
        leftAxis.setAxisMaximum(15f); // the axis maximum is 100

        XAxis xAxis = accChart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        accChart.getXAxis().setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

//        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
//            //private SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss", Locale.GERMAN).format(d);
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                //long millis = TimeUnit.HOURS.toMillis((long) value);
//                Date date = new Date(Float.valueOf(value).longValue());
//                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
//                sdf.setTimeZone(TimeZone.getDefault());
//                String time = sdf.format(date);
//                return time;
//            }
//        };
//        xAxis.setValueFormatter(xAxisFormatter);

        registerReceiver(accValuesReceiver, accValuesIntentFilter);
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
