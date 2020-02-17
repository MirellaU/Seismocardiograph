package com.example.mirella.seismocardiograph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlotActivity extends AppCompatActivity {

    public static final String TAG = "PlotActivity";
    public static String ACC_VALUES = "NEW_ACC_VALUES";

    public double X,Y,Z;

    public ArrayList<Double> accValues = new ArrayList<>();

    ArrayList<Entry> xValues = new ArrayList<>();
    ArrayList<Entry> yValues = new ArrayList<>();
    ArrayList<Entry> zValues = new ArrayList<>();

    public ArrayList<Double> accSaveXValues = new ArrayList<>();
    public ArrayList<Double> accSaveYValues = new ArrayList<>();
    public ArrayList<Double> accSaveZValues = new ArrayList<>();

    public int i = 0,j=0,k=0;
    IntentFilter accValuesIntentFilter;

    private Handler mHandler = new Handler();

    @BindView (R.id.saveID)
    Button saveBtn;
    @BindView(R.id.accXChartID)
    LineChart accXChart;
    @BindView(R.id.accYChartID)
    LineChart accYChart;
    @BindView(R.id.accZChartID)
    LineChart accZChart;

    @OnClick(R.id.saveID)
    public void StopTest() {
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

                xValues.add(new Entry(i, (float) X));
                yValues.add(new Entry(j, (float) Y));
                zValues.add(new Entry(k, (float) Z));

                accSaveXValues.add(X);
                accSaveYValues.add(Y);
                accSaveZValues.add(Z);
                //Log.d(TAG,yValues.toString());
                //Log.d(TAG,zValues.toString());
                //if (xValues.size() > 10) {
                    feedMultiple();

                    //xValues.clear();
                    //yValues.clear();
                    //zValues.clear();
                //}
            }
        }
    };

    private void addAccValuesEntry(LineChart lineChart, ArrayList val) {
        LineData data = lineChart.getData();

        if (data == null) {
            data = new LineData();
            lineChart.setData(new LineData());
        }

        LineDataSet lds = new LineDataSet(val, "X");

        lds.setLineWidth(2.5f);
        lds.setDrawCircles(false);
        lds.setColor(Color.BLUE);
        lds.setHighLightColor(Color.BLUE);
        lds.setValueTextSize(0f);

        data.addDataSet(lds);
        data.notifyDataChanged();

        lineChart.notifyDataSetChanged();

        lineChart.setVisibleXRangeMaximum(100);
        lineChart.moveViewToX(i);

        //val.clear();
        //removeDataSet(lineChart);
        //i++;
    }

    private void feedMultiple(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addAccValuesEntry(accXChart,xValues);
                addAccValuesEntry(accYChart,yValues);
                addAccValuesEntry(accZChart,zValues);
                i++;
                j++;
                k++;
            }
        },200);
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
        accXChart.setKeepPositionOnRotation(true);
        accXChart.getDescription().setEnabled(true);
        accXChart.getDescription().setText("");
        accXChart.getAxisRight().setDrawLabels(false);
        accXChart.getLegend().setEnabled(false);

        final LineData data1 = new LineData();
        accXChart.setData(data1);

        //accChart.fitScreen();
        YAxis leftAxis = accXChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // no grid lines
        leftAxis.setAxisMinimum(-5f); // start at 0
        leftAxis.setAxisMaximum(5f); // the axis maximum is 100

        XAxis xAxis = accXChart.getXAxis();
        xAxis.setDrawGridLines(false); //no grid lines
        accXChart.getXAxis().setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        accYChart.setKeepPositionOnRotation(true);
        accYChart.getDescription().setEnabled(true);
        accYChart.getDescription().setText("");
        accYChart.getAxisRight().setDrawLabels(false);
        accYChart.getLegend().setEnabled(false);

        final LineData data2 = new LineData();
        accYChart.setData(data2);

        YAxis leftAxis2 = accYChart.getAxisLeft();
        leftAxis2.setDrawGridLines(false); // no grid lines
        leftAxis2.setAxisMinimum(-5f); // start at 0
        leftAxis2.setAxisMaximum(5f); // the axis maximum is 100

        XAxis xAxis2 = accYChart.getXAxis();
        xAxis2.setDrawGridLines(false); //no grid lines
        accYChart.getXAxis().setDrawLabels(true);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);

        accZChart.setKeepPositionOnRotation(true);
        accZChart.getDescription().setEnabled(true);
        accZChart.getDescription().setText("");
        accZChart.getAxisRight().setDrawLabels(false);
        accZChart.getLegend().setEnabled(false);

        final LineData data3 = new LineData();
        accZChart.setData(data3);

        YAxis leftAxis3 = accZChart.getAxisLeft();
        leftAxis3.setDrawGridLines(false); // no grid lines
        leftAxis3.setAxisMinimum(-5f); // start at 0
        leftAxis3.setAxisMaximum(5f); // the axis maximum is 100

        XAxis xAxis3 = accZChart.getXAxis();
        xAxis3.setDrawGridLines(false); //no grid lines
        accZChart.getXAxis().setDrawLabels(true);
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
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

    private void Save(){
        String timeStamp = new SimpleDateFormat(getString(R.string.dateFormat)).format(Calendar.getInstance().getTime());
        //String csv = android.os.Environment.getExternalStorageDirectory() + "/Download"+"/dane_z_ACC";
        //Log.d(TAG,csv);
        try {
                //String content = "Separe here integers by semi-colon";

            File directory = getExternalFilesDir(null); //for external storage
            String fileName = "dane_z_ACC.csv";
            File file = new File(directory, fileName);
                // if file doesnt exists, then create it
            if (!directory.exists()) {
                directory.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(timeStamp);
            bw.append("\n");
            bw.append(accSaveXValues.toString());
            bw.append("\n");
            bw.append(accSaveYValues.toString());
            bw.append("\n");
            bw.append(accSaveZValues.toString());
            bw.close();

        } catch (IOException e) {
                e.printStackTrace();
            }
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
