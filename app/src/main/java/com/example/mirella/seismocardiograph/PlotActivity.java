package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "PlotActivity";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    public double X,Y,Z;

    public ArrayList<Double> accValues = new ArrayList<>();

    ArrayList<Entry> xValues = new ArrayList<>();
    ArrayList<Entry> yValues = new ArrayList<>();
    ArrayList<Entry> zValues = new ArrayList<>();

    public int i = 0;

    ArrayList <Float> TESTxValues = new ArrayList<>();

    IntentFilter accValuesIntentFilter;

    private Handler mHandler = new Handler();

    @BindView(R.id.accChartID)
    LineChart accChart;

    private BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                accValues = (ArrayList<Double>) intent.getSerializableExtra("ACC_VALUES");
                X = accValues.get(0);
                Y = accValues.get(1);
                Z = accValues.get(2);

//                Log.d(TAG, String.valueOf(xValues));
//                Log.d(TAG,String.valueOf(yValues));
//                Log.d(TAG,String.valueOf(zValues));

                feedMultiple();
            }
        }
    };

    private void addAccValuesEntry() {
        LineData data = accChart.getData();
//
//        if (data == null) {
//            data = new LineData();
//            accChart.setData(new LineData());
//        }
//
//        LineData pitchData = accChart.getData();
//
//        if (pitchData == null) {
//            pitchData = new LineData();
//            accChart.setData(new LineData());
//        }


        //ArrayList<Entry> zValues = new ArrayList<>();

        //for (int i = 0; i < TESTxValues.size(); i++) {
            zValues.add(new Entry(i, (float) Z));

//        yValues = new Entry(i,(float)Y);
//        zValues = new Entry(i,(float)Z);

            //removeDataSet(accChart);

            //LineDataSet XSet = new LineDataSet(Collections.singletonList(xValues), "X");
            //LineDataSet YSet = new LineDataSet(Collections.singletonList(yValues), "Y");
            LineDataSet ZSet = new LineDataSet(zValues, "Z");

//        XSet.setLineWidth(2.5f);
//        XSet.setDrawCircles(false);
//        //XSet.setCircleRadius(0f);
//        XSet.setColor(Color.RED);
//        //XSet.setCircleColor(Color.BLUE);
//        XSet.setHighLightColor(Color.RED);
//        XSet.setValueTextSize(0f);
//        //XSet.setDrawCircleHole(false);
//        //XSet.setCircleHoleColor(Color.BLUE);
//        XSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
////        XSet.setValueTextColor(Color.RED);
//
//        YSet.setLineWidth(2.5f);
//        YSet.setDrawCircles(false);
//        //XSet.setCircleRadius(0f);
//        YSet.setColor(Color.GREEN);
//        //XSet.setCircleColor(Color.BLUE);
//        YSet.setHighLightColor(Color.GREEN);
//        YSet.setValueTextSize(0f);
//        //XSet.setDrawCircleHole(false);
//        //XSet.setCircleHoleColor(Color.BLUE);
//        YSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
////        XSet.setValueTextColor(Color.RED);

            ZSet.setLineWidth(2.5f);
            ZSet.setDrawCircles(false);
            //XSet.setCircleRadius(0f);
            ZSet.setColor(Color.BLUE);
            //XSet.setCircleColor(Color.BLUE);
            ZSet.setHighLightColor(Color.BLUE);
            ZSet.setValueTextSize(0f);
            //XSet.setDrawCircleHole(false);
            //XSet.setCircleHoleColor(Color.BLUE);
            ZSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

//        data.addDataSet(XSet);
//        //data.notifyDataChanged();
//
//        pitchData.addDataSet(XSet);
            //pitchData.notifyDataChanged();

            //data.addDataSet(XSet);
            //data.addDataSet(YSet);
            data.addDataSet(ZSet);
            data.notifyDataChanged();

            accChart.notifyDataSetChanged();

            accChart.setVisibleXRangeMaximum(200);
            accChart.moveViewToX(i);
        //}
        //

        i++;

        //Log.d(TAG, String.valueOf(xValues));
        //Log.d(TAG,String.valueOf(yValues));
        //Log.d(TAG,String.valueOf(zValues));
//        if(i>500){
//            //i=0;
//            //xValues.clear();
//            //yValues.clear();
//            zValues.clear();
//            Log.d(TAG,zValues.toString());
//        }
    }

    private void feedMultiple(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addAccValuesEntry();
            }
        },100);
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
        accChart.getAxisRight().setDrawLabels(false);
        accChart.getLegend().setEnabled(false);

        final LineData data = new LineData();
        accChart.setData(data);

        //accChart.fitScreen();
        YAxis leftAxis = accChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(-5f); // start at 0
        leftAxis.setAxisMaximum(5f); // the axis maximum is 100

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
