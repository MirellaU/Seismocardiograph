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
import android.widget.Button;
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

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "PlotActivity";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    public double X,Y,Z;

    public ArrayList<Double> accValues = new ArrayList<>();

    public ArrayList<Float> accXValues = new ArrayList<>();
    public ArrayList<Float> accYValues = new ArrayList<>();
    public ArrayList<Float> accZValues = new ArrayList<>();

    public ArrayList<Float> accSaveXValues = new ArrayList<>();
    public ArrayList<Float> accSaveYValues = new ArrayList<>();
    public ArrayList<Float> accSaveZValues = new ArrayList<>();

    IntentFilter accValuesIntentFilter;

    @BindView (R.id.saveID)
    Button saveBtn;
    @BindView(R.id.accXChartID)
    LineChart accXChart;
    @BindView(R.id.accYChartID)
    LineChart accYChart;
    @BindView(R.id.accZChartID)
    LineChart accZChart;

    @OnClick(R.id.saveID)
    public void SaveDataToCSV() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy chcesz zapisać wynik do pliku .csv?")
                .setPositiveButton("Zapisz do pliku", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Save();
                        Toast.makeText(getApplicationContext(), "Zapisano pomyślnie", Toast.LENGTH_LONG).show();
                        accSaveXValues.clear();
                        accSaveYValues.clear();
                        accSaveZValues.clear();
                    }
                })
                .setNegativeButton("Nie zapisuj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

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

                accSaveXValues.add((float)X);
                accSaveYValues.add((float)Y);
                accSaveZValues.add((float)Z);

                addAccValuesEntry(accXChart, accXValues);
                addAccValuesEntry(accYChart, accYValues);
                addAccValuesEntry(accZChart, accZValues);
            }
        }
    };

    private void addAccValuesEntry(LineChart lineChart, ArrayList val) {
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
        lds.setColor(Color.BLUE);
        lds.setHighLightColor(Color.BLUE);
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

    private void removeDataSet(LineChart chart) {
        LineData data = chart.getData();
        if (data != null) {
            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));
            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    private void Save(){
        String timeStamp = new SimpleDateFormat(getString(R.string.dateFormat)).format(Calendar.getInstance().getTime());
        //String csv = android.os.Environment.getExternalStorageDirectory() + "/Download"+"/dane_z_ACC";
        //Log.d(TAG,csv);
        try {
            File directory = getExternalFilesDir(null); //for external storage
            String fileName = "dane_z_ACC.csv";
            File file = new File(directory, fileName);

            // if file in directory not exists, create it
            if (!directory.exists()) {
                directory.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(timeStamp);
            bw.append("\n");
            bw.append("Oś X:");
            bw.append(accSaveXValues.toString());
            bw.append("\n");
            bw.append("Oś Y:");
            bw.append(accSaveYValues.toString());
            bw.append("\n");
            bw.append("Oś Z:");
            bw.append(accSaveZValues.toString());
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void chartConfiguration(LineChart chart, String label){
        chart.setKeepPositionOnRotation(true);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(label);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        accValuesIntentFilter = new IntentFilter("NEW_ACC_VALUES");

        //chart options
        chartConfiguration(accXChart,"Oś X");
        chartConfiguration(accYChart,"Oś Y");
        chartConfiguration(accZChart,"Oś Z");

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
