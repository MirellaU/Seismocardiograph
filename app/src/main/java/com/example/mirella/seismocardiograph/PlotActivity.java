package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "PlotActivity";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    public double X,Y,Z;
    public ArrayList<Double> accValues = new ArrayList<>();
    public ArrayList<Float> accXValues = new ArrayList<>();
    public ArrayList<Float> accYValues = new ArrayList<>();
    public ArrayList<Float> accZValues = new ArrayList<>();

    IntentFilter accValuesIntentFilter;

    static int width; //linear layout width
    static int height; //linear layout height

    //HR detection variables
    public ArrayList<Float> HRValues = new ArrayList<>();
    public ArrayList<Float> HRPlotValues = new ArrayList<>();
    HRDetectionActivity HR = new HRDetectionActivity();
    float f1=15;
    float f2=20;
    int f_samp = 100;
    int N=0;
    int windowLength = 10;
    int HRVal=0;
    int lastHRVal=0;
    int sum=0;
    int i=1;
    public ArrayList<Double> displayedHR = new ArrayList<>();
    String textViewText;

    @BindView(R.id.accXChartID)
    LineChart accXChart;
    @BindView(R.id.accYChartID)
    LineChart accYChart;
    @BindView(R.id.accZChartID)
    LineChart accZChart;
    @BindView(R.id.HRChartID)
    LineChart HRChart;
    @BindView(R.id.HRValue)
    TextView HRValue;
    @BindView(R.id.checkboxXAxis)
    CheckBox checkboxXAxis;
    @BindView(R.id.checkboxYAxis)
    CheckBox checkboxYAxis;
    @BindView(R.id.checkboxZAxis)
    CheckBox checkboxZAxis;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    private BroadcastReceiver accValuesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACC_VALUES)) {
                accValues = (ArrayList<Double>) intent.getSerializableExtra("ACC_VALUES");
                X = accValues.get(0);
                Y = accValues.get(1);
                Z = accValues.get(2);

                accXValues.add((float)X);
                accYValues.add((float)Y);
                accZValues.add((float)Z);
                HRValues.add((float)Z);

                addAccValuesEntry(accXChart, accXValues, Color.BLUE);
                addAccValuesEntry(accYChart, accYValues, Color.GREEN);
                addAccValuesEntry(accZChart, accZValues, Color.RED);
                //addAccValuesEntry(HRChart, HRValues, Color.RED);
                IsCheckBoxChecked();
                if(HRValues.size()>99) {
                    FindPeaks();
                }
            }
        }
    };

    private void FindPeaks(){
        //N= HRValues.size()-1;
        N=6;
        //HR.BandPassFilter(f1, f2, f_samp, N, HRValues);
        BandpassFilterButterworthImplementation BPF = new BandpassFilterButterworthImplementation(f1,f2,N,f_samp);
        for(int i =0;i<HRValues.size();i++){
            BPF.compute(HRValues.get(i));
        }
        HR.SignalSquare(HRValues);
        HR.Smoothing(windowLength, HRValues);
        for(int i=0; i< HRValues.size();i++){
            HRPlotValues.add(HRValues.get(i));
        }
        HRVal = HR.PeakDetection(HRValues);
        Log.d(TAG, "HR w PlotActivity: " + HRVal);
        addHRValuesEntry(HRChart, HRPlotValues, Color.RED);
        HRValues.clear();
        if(HRVal>0) {
            sum=sum+HRVal;
            float val = sum*100/i;
            displayedHR.add(i-1, (double) (val/100*60));
            textViewText = "TĘTNO " + String.format("%1$.0f", displayedHR.get(displayedHR.size()-1));
            HRValue.setText(textViewText);
            i++;
            if(displayedHR.size()>100){
                displayedHR.remove(displayedHR.size()-99);
            }
        }

    }

    public void addAccValuesEntry(LineChart lineChart, ArrayList val, int color) {
        LineData data = lineChart.getData();

        if (data == null) {
            data = new LineData();
            lineChart.setData(new LineData());
        }

        ArrayList<Entry> values = new ArrayList<>();

        for(int i=0 ; i<val.size();i++) {
            values.add(new Entry(i, (float)val.get(i)));
        }
        removeDataSet(lineChart);

        LineDataSet lds = new LineDataSet(values, "");

        lds.setLineWidth(2.5f);
        lds.setDrawCircles(false);
        lds.setColor(color);
        lds.setHighLightColor(color);
        lds.setValueTextSize(0f);

        data.addDataSet(lds);
        data.notifyDataChanged();

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        lineChart.setVisibleXRangeMaximum(100);
        lineChart.moveViewToX(val.size());

        if(val.size()>100) {
            val.remove(val.size()-99);
        }
    }

    public void addHRValuesEntry(LineChart lineChart, ArrayList val, int color) {
        LineData data = lineChart.getData();

        if (data == null) {
            data = new LineData();
            lineChart.setData(new LineData());
        }

        ArrayList<Entry> values = new ArrayList<>();

        for(int i=0 ; i<val.size();i++) {
            values.add(new Entry(i, (float)val.get(i)));
        }
        removeDataSet(lineChart);

        LineDataSet lds = new LineDataSet(values, "");

        lds.setLineWidth(2.5f);
        lds.setDrawCircles(false);
        lds.setColor(color);
        lds.setHighLightColor(color);
        lds.setValueTextSize(0f);

        data.addDataSet(lds);
        data.notifyDataChanged();

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        lineChart.setVisibleXRangeMaximum(200);
        lineChart.moveViewToX(val.size());

        if(val.size()>200) {
            val.remove(val.size()-199);
        }
    }

    private void removeDataSet(LineChart chart) {
        LineData data = chart.getData();
        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    private void chartConfiguration(LineChart chart, String label){
        chart.setKeepPositionOnRotation(true);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(label);
        chart.getDescription().setTextSize(20);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.fitScreen();

        final LineData data = new LineData();
        chart.setData(data);

        //accChart.fitScreen();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(-1f); // start at -1
        leftAxis.setAxisMaximum(1f); // the axis maximum is 1

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        chart.getXAxis().setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            // @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.GERMAN);
                String time = sdf.format(new Date());
                return time;
            }
        };
        xAxis.setValueFormatter(xAxisFormatter);
    }

    private void HRchartConfiguration(LineChart chart, String label){
        chart.setKeepPositionOnRotation(true);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(label);
        chart.getDescription().setTextSize(20);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.fitScreen();

        final LineData data = new LineData();
        chart.setData(data);

        //accChart.fitScreen();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(0f); // start at -1
        leftAxis.setAxisMaximum(0.2f); // the axis maximum is 1

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        chart.getXAxis().setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            // @Override
            public String getFormattedValue(float value, AxisBase axis) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.GERMAN);
                String time = sdf.format(new Date());
                return time;
            }
        };
        xAxis.setValueFormatter(xAxisFormatter);
    }

    public void IsCheckBoxChecked()
    {
        width=linearLayout.getWidth();
        height=linearLayout.getHeight();

        if(checkboxXAxis.isChecked()){
            accXChart.setVisibility(View.VISIBLE);
            accXChart.setLayoutParams(new LinearLayout.LayoutParams(width,700));

        }else{
            accXChart.setVisibility(View.INVISIBLE);
            accXChart.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        if (checkboxYAxis.isChecked()) {
            accYChart.setVisibility(View.VISIBLE);
            accYChart.setLayoutParams(new LinearLayout.LayoutParams(width,700));
        }else{
            accYChart.setVisibility(View.INVISIBLE);
            accYChart.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
        if (checkboxZAxis.isChecked()) {
            accZChart.setVisibility(View.VISIBLE);
            accZChart.setLayoutParams(new LinearLayout.LayoutParams(width,700));
        }else{
            accZChart.setVisibility(View.INVISIBLE);
            accZChart.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        accValuesIntentFilter = new IntentFilter("NEW_ACC_VALUES");
        registerReceiver(accValuesReceiver, accValuesIntentFilter);

        accXChart.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        accYChart.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        accZChart.setLayoutParams(new LinearLayout.LayoutParams(1050,700));
        HRChart.setLayoutParams(new LinearLayout.LayoutParams(1050,700));

        //chart options
        chartConfiguration(accXChart,"Oś X");
        chartConfiguration(accYChart, "Oś Y");
        chartConfiguration(accZChart, "Oś Z");
        HRchartConfiguration(HRChart, "Tętno");
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
